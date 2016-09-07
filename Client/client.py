"""Secure client implementation

This is a skeleton file for you to build your secure file store client.

Fill in the methods for the class Client per the project specification.

You may add additional functions and classes as desired, as long as your
Client class conforms to the specification. Be sure to test against the
included functionality tests.
"""

from base_client import BaseClient, IntegrityError
from crypto import CryptoError
import util
from insecure_client import path_join
import pdb

class Client(BaseClient):
    def __init__(self, storage_server, public_key_server, crypto_object,
                 username):
        super().__init__(storage_server, public_key_server, crypto_object,
                         username)
        """
        --<user>
                \---<meta>
                            \----<keys>     
                            |----<signs>
                \---file---<meta>       Hk_a(Ek_e(value))
                         \-<data>       Ek_e(value)
                         \-<key>        [k_e, k_a] for this file 
                         \-<share>      list of users file shared with
                         \-<F(user1)>
                         \  ...
                         \-<F(userN)>   key encripted with user N pub_key
                
        """ 
        self.pub_key = public_key_server.get_public_key(self.username)
        keys = {}                 
        keys["k_n"] = self.crypto.get_random_bytes(16)
      
        keys_encr = self.crypto.asymmetric_encrypt(util.to_json_string(keys), self.pub_key)
        s_keys_encr = self.crypto.asymmetric_sign(keys_encr, self.private_key)
        self.storage_server.put(self.username + "<meta>keys", keys_encr)
        self.storage_server.put(self.username + "<meta>sign", s_keys_encr)
        
    def upload(self, name, value):
        keys = self.read_keys()
        #print("*** uid ", uid, exist)
        file_name = self.crypto.message_authentication_code(path_join(self.username, name), keys["k_n"], "SHA256")
        file_id, exist = self.resolve(file_name)
        if exist:
            k = self.download_key(file_name, self.private_key)
            k_f, k_s = k
        else:
            file_id = self.crypto.get_random_bytes(16)
            k_f = self.crypto.get_random_bytes(16)      # to encode file
            k_s = self.crypto.get_random_bytes(16)      # to sign file   
            self.upload_assim(file_name + "<key>", [k_f,k_s], self.pub_key) 
            self.upload_assim(file_name, "[POINTER] " + file_id, self.pub_key)
            
        self.upload_data(file_id, value, [k_f,k_s])
        
        # print(self.username, " uploads ")
        # print("----f_n ", file_name)
        # print("with value ---- ", value, file_id)
        return True
    
    def download(self, name):
        keys = self.read_keys()
        #pdb.set_trace()
        file_name = self.crypto.message_authentication_code(path_join(self.username, name), keys["k_n"], "SHA256")
        try:
            uid, value = self.resolve(file_name)
        except:
            #raise IntegrityError("The value has been tampered with")
            return None
        # print(value)
        return value if value else None

        
    # Share to bob message: Eb( file_id, F(bob/name)] )
    def share(self, user, name):
        keys = self.read_keys()
        alice_name = self.crypto.message_authentication_code(path_join(self.username, name), keys["k_n"], "SHA256")  
        bob_name = self.crypto.message_authentication_code(path_join(user, name), keys["k_n"], "SHA256")
        
        file_id, exist = self.resolve(alice_name)
        k = self.download_key(alice_name, self.private_key)
        bob_pub_key = self.pks.get_public_key(user)
        self.upload_assim(bob_name + "<key>", k, bob_pub_key) 
        
        enc_message = self.crypto.asymmetric_encrypt(util.to_json_string([file_id, bob_name]), bob_pub_key) 
        sign_mesage = self.crypto.asymmetric_sign(enc_message, self.private_key)
        message = util.to_json_string([enc_message, sign_mesage])
        
        
        share_with = self.download_data(alice_name + "<share>", k)
        if share_with:
            share_with = util.from_json_string(share_with)
        else:
            share_with = {}
        share_with[user] = bob_name
        self.upload_data(alice_name + "<share>", util.to_json_string(share_with),k)
        
        #print(self.username, " shares ", name, " key = ", k_f)
        return message
        
        # Bob receive share: 
        # F(bob/b_name):Eb([POINTER] file_id)
        # F(bob/b_name)<key>:Eb([POINTER] [F(bob/name)])    
    def receive_share(self, from_username, newname, message):
        keys = self.read_keys()
        alice_pub_key = self.pks.get_public_key(from_username)
        receive_name = path_join(self.username, newname)
        file_name = self.crypto.message_authentication_code(receive_name, keys["k_n"], "SHA256")
        
        enc_message, sign_mesage = util.from_json_string(message)
        if not self.crypto.asymmetric_verify(enc_message, sign_mesage, alice_pub_key):
            raise IntegrityError("Bad Message")
        message = self.crypto.asymmetric_decrypt(enc_message, self.private_key)
        file_id, bob_name = util.from_json_string(message)
        
        self.upload_assim(file_name + "<key>", "[POINTER] " + bob_name, self.pub_key) 
        self.upload_assim(file_name, "[POINTER] " + file_id, self.pub_key)
        #print(self.username, " receives ", newname, " key = ", k_f)        

    # Revoke: Change key for other, including go to child's share dictionary
    def revoke(self, user, name):
        keys = self.read_keys()
        alice_name = self.crypto.message_authentication_code(path_join(self.username, name), keys["k_n"], "SHA256")  
        file_id, value = self.resolve(alice_name) 
        old_k = self.download_key(alice_name, self.private_key)        

        new_k = [self.crypto.get_random_bytes(16), self.crypto.get_random_bytes(16)]   
        self.upload_assim(alice_name + "<key>", new_k, self.pub_key) 
        self.upload_data(file_id, value, new_k)
        
        share_with = self.download_data(alice_name + "<share>", old_k)
        if share_with:
            share_with = util.from_json_string(share_with)
            bob_name = share_with.pop(user)
            self.storage_server.delete(bob_name + "<key>")         
            self.upload_data(alice_name + "<share>", util.to_json_string(share_with),new_k)
            
            self.update_childs_key(share_with, old_k, new_k)
       
    
    def resolve(self, uid):
        keys = self.read_keys()

        # pdb.set_trace()
        k = self.download_key(uid, self.private_key)
        if k is None:
            return uid, None
            
        file_id = self.download_pointer(uid, self.private_key)
        # print(file_id, "\n")

        if file_id and file_id.startswith("[POINTER]"):
            value = self.download_data(file_id[10:], k)
            return file_id[10:], value
        else:
            raise IntegrityError()
                
    # reads keys dictionary from storage server
    def read_keys(self):
        k = self.storage_server.get(self.username + "<meta>keys")
        signed_k = self.storage_server.get(self.username + "<meta>sign")
        if not self.crypto.asymmetric_verify(k, signed_k, self.pub_key):
            raise IntegrityError("The KEYS value has been tampered with")
        try:
            k = self.crypto.asymmetric_decrypt(k, self.private_key)
            k = util.from_json_string(k)
        except:
            raise IntegrityError("The KEYS value has been tampered with")
            
        return k  

    def upload_data(self, file_name, value, key):
        k_f, k_s = key
        val_IV = self.crypto.get_random_bytes(16)
        enc_val = self.crypto.symmetric_encrypt(value, k_f, cipher_name='AES', mode_name='CBC', IV= val_IV)
        self.storage_server.put(file_name, util.to_json_string([val_IV, enc_val]))
        self.upload_meta(file_name, enc_val, k_s)
        
        return enc_val
        
    def upload_meta(self, file_name, enc_val, key):      
        signed_val = self.crypto.message_authentication_code(enc_val, key, "SHA256")       
        self.storage_server.put(file_name + "<meta>", signed_val) 

    def upload_assim(self, file_name, keys, key):
        encoded_keys = self.crypto.asymmetric_encrypt(util.to_json_string(keys), key)    
        self.storage_server.put(file_name, encoded_keys)     
    
    def download_data(self, file_name, keys):
        k_f, k_s = keys
        val_lst = self.storage_server.get(file_name)
        signed_val = self.storage_server.get(file_name + "<meta>")

        if val_lst is None or signed_val is None:
            return None
        IV, enc_val = util.from_json_string(val_lst)
        self.check_integrity(signed_val, enc_val, k_s)
 
        val = self.crypto.symmetric_decrypt(enc_val, k_f, cipher_name='AES', mode_name='CBC', IV=IV)
        return val
        
    def check_integrity(self, signed_val, enc_val, key):
        hashed_val = self.crypto.message_authentication_code(enc_val, key, "SHA256")
        if signed_val != hashed_val:
            raise IntegrityError()
            
    def download_key(self, file_name, key):
        is_creator = False
        k = self.storage_server.get(file_name + "<key>")
        if k is None:
            return None
        try:
            k = self.crypto.asymmetric_decrypt(k, key)
            k = util.from_json_string(k)
        except:
            raise IntegrityError("The KEYS value has been tampered with")  
        # [,] or "[POINTER] 233154"       
        if isinstance(k, str) and k.startswith("[POINTER]"):
            k = self.storage_server.get(k[10:] + "<key>")
            if k is None:
                return None
            try:
                k = self.crypto.asymmetric_decrypt(k, key)
                k = util.from_json_string(k)
            except:
                raise IntegrityError("The KEYS value has been tampered with")  
        else:
            is_creator = True
        return k
    
    def download_pointer(self, file_name, key):
        is_creator = False
        p = self.storage_server.get(file_name)
        #pdb.set_trace()
        if p is None:
            return None
        try:
            p = self.crypto.asymmetric_decrypt(p, key)
            p = util.from_json_string(p)
        except:
            raise IntegrityError("The KEYS value has been tampered with")     
        return p

    # F(alice/name)<share>:Ek({bob:F(bob/name),carol:F(carol/name)})    
    def update_childs_key(self, childs, old_k, new_k):
        if len(childs) == 0:
            return
        for ch in childs.keys():
            child_pub_key = self.pks.get_public_key(ch)
            self.upload_assim(childs[ch] + "<key>", new_k, child_pub_key) 
            share_with = self.download_data(childs[ch] + "<share>", old_k)
            if share_with:
                self.upload_data(alice_name + "<share>", share_with, new_k)
                self.update_childs_key(util.from_json_string(share_with), old_k, new_k)