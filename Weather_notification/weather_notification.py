import os
import requests
from twilio.rest import Client
import pickle


ACCUWEATHER_API_KEY = os.environ['ACCUWEATHER']
TWILIO_NUMBER = os.environ['TWILIO_NUMBER']
TWILIO_SID = os.environ['TWILIO_SID']
TWILIO_TOKEN = os.environ['TWILIO_TOKEN']
my_number = os.environ['MY_NUMBER']
LOCATION_KEYS = {'San Mateo': 337235, "South San Francisco": 337259}
location_to_forecast = {}

def get_forecast_data():
    forecast_data = {}
    for location, location_key in LOCATION_KEYS.iteritems():
        print "Getting daily forcast for {}".format(location)
        url = "http://dataservice.accuweather.com/forecasts/v1/daily/1day/{}".format(location_key)
        payload={ "apikey" : ACCUWEATHER_API_KEY,
                  "details" : True,
                  "metric" : False 
                }
        resp = requests.get(url = url, params = payload)
        forecast_data[location] = resp.json()
    return forecast_data

def process_forecast_data(forecast_data):
    for location,data in forecast_data.iteritems():
        min_temp = data['DailyForecasts'][0]['Temperature']['Minimum']['Value']
        max_temp = data['DailyForecasts'][0]['Temperature']['Maximum']['Value']
        day_cond = data['DailyForecasts'][0]['Day']['IconPhrase']
        night_cond = data['DailyForecasts'][0]['Night']['IconPhrase']
        link = data['DailyForecasts'][0]['MobileLink']
        print min_temp, max_temp, day_cond, night_cond
        location_to_forecast[location] = [min_temp, max_temp, day_cond, night_cond, link]

def build_a_message():
    message = ''
    for location, data in location_to_forecast.iteritems():
        message = '''{}In {} the high will  be {} with {} conditions during the day, the low will be {} with {} conditions during the night. Follow the link for more
info: {}\n'''.format(message, location, data[1], data[2], data[0], data[3], data[4])
    return message

def text_myself():
    weather = build_a_message()
    twilio = Client(TWILIO_SID, TWILIO_TOKEN)
    message = twilio.api.account.messages.create(body=weather, from_=TWILIO_NUMBER, to=my_number)

def main():
    forecast_data = get_forecast_data()
    pickle.dump(forecast_data, open('my_sample_data.p', 'w'))
        #forecast_data = pickle.load(open('my_sample_data.p', 'r'))
    process_forecast_data(forecast_data)
    build_a_message()
    text_myself()

if __name__ == '__main__':
    main()