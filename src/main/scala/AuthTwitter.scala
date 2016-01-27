package AuthTwitter

import org.apache.spark._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._


/**
 * Created by davidsuarez on 27/01/16.
 */

object AuthTwitter {
  def main(args: Array[String]) {

    if (args.length < 2) {
      System.err.println("Usage: AuthTwitter <hostname> <port>")
      System.exit(1)
    }

    val conf = new SparkConf().setMaster("local[2]").setAppName("innoCharacter")
    val ssc = new StreamingContext(conf, Seconds(3))

    // Create a DStream that will connect to hostname:port, like localhost:9999
    val lines = ssc.socketTextStream(args(0), args(1).toInt, StorageLevel.MEMORY_AND_DISK_SER)

    // Split each line into words
    val words = lines.flatMap(_.split(" "))

    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)

    // Print the first ten elements of each RDD generated in this DStream to the console
    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()

  }
}

