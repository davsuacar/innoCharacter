CREATE KEYSPACE streaming_test
WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

CREATE TABLE tweets (
  tweet text PRIMARY KEY,
  contributors text,
  created_at timestamp,
  current_user_retweet_id int,
  geolocation text,
  id int,
  in_reply_to_screen_name text,
  in_reply_to_status_id int,
  in_reply_to_user_id int,
  lang text,
  place text,
  quoted_status text,
  quoted_status_id int,
  retweet_count int,
  retweeted_status text,
  scopes text,
  user text,
  with_held_in_countries text,
  is_favourite boolean,
  is_possibly_sensitive boolean,
  is_retweeted_by_me boolean
);
