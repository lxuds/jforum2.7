--
-- Table structure for table 'jforum_spam'
--
DROP TABLE IF EXISTS jforum_spam;
CREATE TABLE jforum_spam (
  pattern VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

ALTER TABLE jforum_topics MODIFY topic_title VARCHAR(120);
ALTER TABLE jforum_posts_text MODIFY post_subject VARCHAR(130);