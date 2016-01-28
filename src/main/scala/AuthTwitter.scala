package AuthTwitter

import org.apache.spark._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import twitter4j.TwitterFactory
import twitter4j.auth.{OAuth2Authorization, AuthorizationFactory, Authorization}


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

    val twitterAuth = new twitter4j.conf.ConfigurationBuilder()
      .setOAuthConsumerKey("")
      .setOAuthConsumerSecret("")
      .setOAuthAccessToken("")
      .setOAuthAccessTokenSecret("")
      .build

    val twitter_auth = new TwitterFactory(twitterAuth)
    val a = new twitter4j.auth.OAuthAuthorization(twitterAuth)
    val atwitter : Option[twitter4j.auth.Authorization] =  Some(twitter_auth.getInstance(a).getAuthorization())

    val filters = Seq("@DavidIntelygenz")
    val twitterStream = TwitterUtils.createStream(ssc, atwitter, filters, StorageLevel.MEMORY_AND_DISK)

    // Split each line into words
    val words = twitterStream.flatMap(status => {
      println("TWITTER---->" + status.getText)
      status.getText.split(" ")
    })

    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)

    // Print the first ten elements of each RDD generated in this DStream to the console
    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()

  }
}

