"""
Collects tweets and puts them into the specified json file.
FORMAT
data["tweets"] = []
data["tweets"].append({
			'userid' : user's id, can be used to pull user info later
			'username' : username
			'text' : tweet's text
			'has_link' : does it have a link?
			'link_title' : title of the link, blank if no link
			'coordinates': coordinates of the tweet's location
			''
			

"""

import tweepy
import json
import time 

consumer_key = "AfmRQQimxa1YiAEM4ai4ntJVm"
consumer_secret = "BbUWIdzxKjVzPoR5pND0CRwexsvjEkMj7aj18pvL6WSolfbdhS" 
access_token = "2662279136-1Ny7IWoNCUt9W3Wdcm2mUVggostmIwVFyCXY6i7"
access_secret_token = "j3qSpt2rtGlN693VkkG7kkL6WjIbvwpcVpHrn9M5AlnB1"

auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_secret_token)

api = tweepy.API(auth)


public_tweets = api.home_timeline()

data = {}
data['tweets'] = []

class MyStreamListener(tweepy.StreamListener):
	def __init__(self, limit = 1000):
		self.curr_count = 0
		self.limit = limit
		super(MyStreamListener, self).__init__()
	def on_status(self, tweet):
		if (self.curr_count > self.limit):
			saveFile = open('1000_tweets.json', 'a')
			json.dump(data, saveFile)
			saveFile.close()
			return False
		if (not tweet.text.startswith('RT') and (tweet.place != None) and (tweet.place.bounding_box != None)):
			temp = {}
			print("NEW TWEET")
			print("USER: ", tweet.user.name)
			temp['userid'] = tweet.user.id
			temp['username'] = tweet.user.name
			if (tweet.truncated):
				print(tweet.extended_tweet['full_text'])
				temp['text'] = tweet.extended_tweet['full_text']
			else:
				print(tweet.text)
				temp['text'] = tweet.text
			print(tweet.place)
			print(tweet.place.bounding_box.coordinates)
			temp['coordinates'] = tweet.place.bounding_box.coordinates
			temp['has_link'] = False
			temp['link'] = ""
			temp['link_title'] = ""	
			if (len(tweet.entities['urls']) > 0):
				if (not tweet.entities['urls'][0]['expanded_url'].startswith('https://twitter.com/')):
					temp['has_link'] = True
					temp['link'] = tweet.entities['urls'][0]['expanded_url']
					print("LINK-> ", tweet.entities['urls'][0]['expanded_url'])
			data['tweets'].append(temp)
			self.curr_count += 1
	def on_error(self, status_code):
		print("SAM ERROR: TOO MANY REQUESTS")
		return False
myStreamListener = MyStreamListener()
myStream = tweepy.Stream(auth = api.auth, listener=myStreamListener)

myStream.filter(locations=[-124.4091373995,32.6056348189,-114.5742952571,42.0042762452])









