import pymongo
import sys

from pymongo import MongoClient
client = MongoClient()
db = client['scraping']  #get the database


lang = sys.argv[1]

## The database consists of 3 different collections
# 1. tweets
# 2. blog posts
# 3. forum posts

tweets = db['tweets']
blog_posts = db['blogPosts']
forum_posts = db['forumPosts']


# fetch all documents in each collection
# languageCode codes are the same as in the ../scraping folder

for post in blog_posts.find({"languageCode": lang}):
	print post['data'].encode("utf-8")

for post in forum_posts.find({"languageCode": lang}):
	print post['data'].encode("utf-8")

for tweet in tweets.find({"languageCode": lang}):
	print tweet['data'].encode("utf-8")

