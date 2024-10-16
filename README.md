Spark Scala Inverted Index Project
Project Overview
This repository contains a Spark-based project written in Scala that reads the content of a collection of text files, processes them to build an inverted index, and stores the resulting data in MongoDB. The inverted index allows for efficient querying of documents containing specific words.

Key Features:
Input: The project reads multiple text files into a single RDD (this data is saved in the path : data/documents).
Inverted Index: It creates an inverted index, where each word from the collection of files is associated with the documents that contain it.
Output Format: The inverted index is saved as multiple text files in the wholeInvertedIndex folder.
MongoDB Integration: After the inverted index is generated, it is saved in a MongoDB collection with a specific format.
Query Support: A file (mongo_search_query.txt) contains MongoDB queries that allow you to search for specific words and retrieve the documents that contain them.
Inverted Index Format
The inverted index is stored in files under the wholeInvertedIndex directory. Each file contains entries in the following format:
Word, Count(Word), Document_list
Where:
Word: A single word from the collection of files.
Count(Word): The number of documents containing the Word.
Document_list: A list of document names (file names) that contain the Word.

Example:
analysis, 3, doc10, doc4, doc7
//This means that the word "analysis" appears in 3 different documents: doc10, doc4, and doc7.

MongoDB Storage Format
After the inverted index is generated, it is saved in MongoDB with the following structure:

json
Copy code
{
  "_id": {
    "$oid": "67100612324f2d319955bede"
  },
  "word": "word",
  "count": int,
  "documentList": [
    "doc1",
    "doc2",
    "doc4"
  ]
}
Where:

word: The specific word from the text files.
count: The number of documents containing the word.
documentList: A list of document names where the word appears.
MongoDB Query
The mongo_search_query.txt file contains MongoDB queries to search for specific words and output the list of documents that contain those words. These queries can be used to quickly retrieve the relevant documents from the database based on user input.

please remember to add your database name and collection name in MongoDB Connector (spark variable):
      .config("spark.mongodb.write.connection.uri", "mongodb://localhost:27017/test2/assigment1")

and in mongo.write 
  .option("database", "test2")
  .option("collection", "assigment1")
      
Run the Project: Execute the Scala program to read the files, build the inverted index, and store the results in MongoDB.
Query MongoDB: Use the queries provided in mongo_search_query.txt to retrieve document lists for specific words.

Folder Structure
/wholeInvertedIndex
  - part-00000
  - part-00001
  - ...
/src
  - Main.scala
mongo_search_query.txt

Dependencies:
check the build.sbt file and make sure that the scala varsion is "2.12.8" 
and include those Dependencies:
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.3.0",
    libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.3.0",
    libraryDependencies += "org.mongodb.spark" %% "mongo-spark-connector" % "10.1.1"
