package com.scaleset.cfbuilder.beanstalk;

/**
 * Constructs a {@code SourceBundle} that is an embedded property of the ElasticBeanstalk {@link ApplicationVersion}.
 *
 * @see <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/aws-properties-beanstalk-sourcebundle.html">Documentation
 * Reference</a>
 */
public class SourceBundle {
    private String s3Bucket;
    private String s3Key;

    public SourceBundle(String s3Bucket, String s3Key) {
        this.s3Bucket = s3Bucket;
        this.s3Key = s3Key;
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public SourceBundle setS3Bucket(String s3Bucket) {
        this.s3Bucket = s3Bucket;
        return this;
    }

    public String getS3Key() {
        return s3Key;
    }

    public SourceBundle setS3Key(String s3Key) {
        this.s3Key = s3Key;
        return this;
    }
}
