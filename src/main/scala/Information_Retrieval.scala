import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

// Define a case class to represent the structure of your DataFrame
case class WordData(word: String, count: Int, documentList: Array[String])

object Information_Retrieval {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("ReadMultipleTextFiles").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // Function to extract the file name from the full path
    def getFileName(path: String): String = {
      val parts = path.split("/") // On Windows, you may need "\\\\" instead of "/"
      val filePath = parts.last // Extract the last part, which is the file name
      filePath.split("\\.").head
    }

    val inputRDD = sc.wholeTextFiles("data/documents/*").flatMap {
      case (path, content) => {
        val fileName = getFileName(path)
        content
          // split on non-word characters, clean, and lowercase at once
          .toLowerCase.split("[\\W_]+")
          // split by non-word characters and underscore
          .filter(word => word.nonEmpty)
          // filter out empty words resulting from splitting
          .map(word =>(word,(1, List(fileName))))
      }
    }

      val formatedData=inputRDD.reduceByKey{
      case ((count1, docs1), (count2, docs2)) =>
        (count1 + count2, (docs1 ++ docs2).distinct.sorted) // Combine counts and merge document lists
    }.sortBy(tuple => tuple._1)

    // Transform the RDD to the desired output format: Word, Count(Word), Document_list
    val textFileFormat = formatedData.map {
      case (word, (count, docs)) =>
        s"$word, $count, ${docs.mkString(", ")}" // Create the output string
    }

//    outputRDD.collect().take(60).foreach(
//      x => {
//        println(x)
//      }
//    )

//    System.exit(0)
      textFileFormat.saveAsTextFile("data/wholeInvertedIndex")

    // Initialize SparkSession
    val spark = SparkSession.builder()
      .appName("InvertedIndexToMongo")
      .config("spark.mongodb.write.connection.uri", "mongodb://localhost:27017/test2/assigment1")
      .getOrCreate()


    import spark.implicits._

    // Transform the RDD to case class instances
    val dfRDDFormat = formatedData.map {
      case (word, (count, docs)) =>
        WordData(word, count, docs.toArray)
    }
    val outputDF = dfRDDFormat.toDF()
    // Show the DataFrame
//    outputDF.show()

    // Save DataFrame to MongoDB
    outputDF.write
      .format("mongodb")
      .mode("append")
      .option("database", "test2")
      .option("collection", "assigment1")
      .save()

    

  }
}
