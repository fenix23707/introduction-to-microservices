#!/bin/sh

awslocal s3 mb "s3://${AWS_S3_BUCKET_NAME}"
echo "✅ Bucket '${AWS_S3_BUCKET_NAME}' created!"

