package AuthTwitter

import com.datastax.spark.connector.SomeColumns
import org.apache.spark._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import twitter4j.TwitterFactory
import twitter4j.auth.{OAuth2Authorization, AuthorizationFactory, Authorization}
import com.datastax.spark.connector.streaming._


/**
 * Created by davidsuarez on 27/01/16.
 */

object AuthTwitter {

  def main(args: Array[String]) {

    val CassandraConnectionIp = "cassandra"

    val conf = new SparkConf().setMaster("local[2]").setAppName("innoCharacter").set("spark.cassandra.connection.host", CassandraConnectionIp)
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

    val filters = Seq()
    val twitterStream = TwitterUtils.createStream(ssc, atwitter, filters, StorageLevel.MEMORY_AND_DISK)

    // Split each line into words
    val tweets = twitterStream.filter(status => status.getLang == "en").map(status => {
      println("Tweet INFO-->\n" +
        "Tweet getText " + status.getText + "\n" +
        "Tweet getContributors " + status.getContributors + "\n" +
        "Tweet getCreatedAt " + status.getCreatedAt + "\n" +
        "Tweet getCurrentUserRetweetId " + status.getCurrentUserRetweetId + "\n" +
        "Tweet getGeoLocation " + status.getGeoLocation + "\n" +
        "Tweet getId " + status.getId + "\n" +
        "Tweet getInReplyToScreenName " + status.getInReplyToScreenName + "\n" +
        "Tweet getInReplyToStatusId " + status.getInReplyToStatusId + "\n" +
        "Tweet getInReplyToUserId " + status.getInReplyToUserId + "\n" +
        "Tweet getLang " + status.getLang + "\n" +
        "Tweet getPlace " + status.getPlace + "\n" +
        "Tweet getQuotedStatus " + status.getQuotedStatus + "\n" +
        "Tweet getQuotedStatusId " + status.getQuotedStatusId + "\n" +
        "Tweet getRetweetCount " + status.getRetweetCount + "\n" +
        "Tweet getRetweetedStatus " + status.getRetweetedStatus + "\n" +
        "Tweet getScopes " + status.getScopes + "\n" +
        "Tweet getUser " + status.getUser + "\n" +
        "Tweet getWithheldInCountries " + status.getWithheldInCountries + "\n" +
        "Tweet isFavorited " + status.isFavorited + "\n" +
        "Tweet isPossiblySensitive " + status.isPossiblySensitive + "\n" +
        "Tweet isRetweetedByMe: " + status.isRetweetedByMe + "\n"
      )
      (status.getText,
        status.getContributors,
        status.getCreatedAt,
        status.getCurrentUserRetweetId,
        status.getGeoLocation,
        status.getId,
        status.getInReplyToScreenName,
        status.getInReplyToStatusId,
        status.getInReplyToUserId,
        status.isFavorited,
        status.isPossiblySensitive,
        status.isRetweetedByMe,
        status.getLang,
        status.getPlace,
        status.getQuotedStatus,
        status.getQuotedStatusId,
        status.getRetweetCount,
        status.getRetweetedStatus,
        status.getScopes,
        status.getUser,
        status.getWithheldInCountries)
    })

    tweets.saveToCassandra("streaming_test", "tweets")

    ssc.start()
    ssc.awaitTermination()

  }
}

