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
                \---file---<meta>
                         \-<data>
                         \-<key>
                
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
        #pdb.set_trace()
        uid, exist = self.resolve(path_join(self.username, name))
        #print("*** uid ", uid, exist)
        file_name = self.crypto.message_authentication_code(uid, keys["k_n"], "SHA256")
        if exist:
            pointer_name = self.crypto.message_authentication_code( path_join(self.username, name), keys["k_n"], "SHA256")
            k = self.download_key(pointer_name, self.private_key)
            #print("Key ", k[0])
            k_f, k_s = k
            file_name = uid
        else:
            k_f = self.crypto.get_random_bytes(16)      # to encode file
            k_s = self.crypto.get_random_bytes(16)      # to sign file   
            self.upload_key(file_name, [k_f,k_s], self.pub_key) 
            
        enc_val = self.upload_data(file_name, "[DATA] " + value, k_f)
        self.upload_meta(file_name, enc_val, k_s)

        # print(self.username, " uploads ")
        # print("----f_n ", file_name)
        
        return True
    
    def download(self, name):
        # print(self.username, " downloads ", name)
        try:
            uid, value = self.resolve(path_join(self.username, name))
        except:
            raise IntegrityError("The value has been tampered with")
        return value[7:] if value else None

        
    def share(self, user, name):
        keys = self.read_keys()
        file_name = self.crypto.message_authentication_code(path_join(self.username, name), keys["k_n"], "SHA256")   
        k = self.download_key(file_name, self.private_key)
        k_f, k_s = k
        bob_pub_key = self.pks.get_public_key(user)
        
        sharename = path_join(self.username, "sharewith", user, name)
        pointer_name = self.crypto.message_authentication_code(sharename, keys["k_n"], "SHA256")
        pointer_message = path_join("[POINTER] [SHARE] ", file_name)
        
        enc_mes = self.upload_data(pointer_name, pointer_message, k_f)
        self.upload_meta(pointer_name, enc_mes, k_s)
        self.upload_key(pointer_name, k, bob_pub_key)
        #print(self.username, " shares ", name, " key = ", k_f)
        return pointer_name
        
        
    def receive_share(self, from_username, newname, message):
        keys = self.read_keys()
        k = self.download_key(message, self.private_key)
        k_f, k_s = k
        receive_name = path_join(self.username, newname)
        pointer_name = self.crypto.message_authentication_code(receive_name, keys["k_n"], "SHA256")    
        pointer_message = path_join("[POINTER] [RECIV] ", message)
        
        enc_mes = self.upload_data(pointer_name, pointer_message, k_f)
        self.upload_meta(pointer_name, enc_mes, k_s)
        self.upload_key(pointer_name, k, self.pub_key)
        #print(self.username, " receives ", newname, " key = ", k_f)        


    def revoke(self, user, name):
        sharename = path_join(self.username, "sharewith", user, name)
        self.storage_server.delete(sharename)
    
    def resolve(self, uid):
        keys = self.read_keys()
        is_pointer = False
        while True:
            # print("XXX", uid)    
            if uid.startswith("[POINTER]"):
                uid = value[19:]
                file_name = uid
            else:
                file_name = self.crypto.message_authentication_code(uid, keys["k_n"], "SHA256")
                
            # pdb.set_trace()
            if not is_pointer:
                k = self.download_key(file_name, self.private_key) 
            if k is None:
                return uid, None
            # print(k[0])
            value = self.download_data(file_name, k)

            if value.startswith("[DATA]"):
                return uid, value
            elif value.startswith("[POINTER]"):
                uid = value
                is_pointer = True
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
        val_IV = self.crypto.get_random_bytes(16)
        enc_val = self.crypto.symmetric_encrypt(value, key, cipher_name='AES', mode_name='CBC', IV= val_IV)
        self.storage_server.put(file_name + "<data>", util.to_json_string([val_IV, enc_val]))
        return enc_val
        
    def upload_meta(self, file_name, enc_val, key):      
        signed_val = self.crypto.message_authentication_code(enc_val, key, "SHA256")       
        self.storage_server.put(file_name + "<meta>", signed_val) 

    def upload_key(self, file_name, keys, key):
        encoded_keys = self.crypto.asymmetric_encrypt(util.to_json_string(keys), key)    
        self.storage_server.put(file_name + "<key>", encoded_keys)     
    
    def download_data(self, file_name, keys):
        k_f, k_s = keys
        val_lst = self.storage_server.get(file_name + "<data>")
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
        k = self.storage_server.get(file_name + "<key>")
        if k is None:
            return None
        try:
            k = self.crypto.asymmetric_decrypt(k, key)
            k = util.from_json_string(k)
        except:
            raise IntegrityError("The KEYS value has been tampered with")        
        return k