#!/bin/bash
awslocal s3 mb "s3://${RESOURCE_SERVICE_AWS_S3_BUCKET_NAME}"
echo "✅ Bucket '${RESOURCE_SERVICE_AWS_S3_BUCKET_NAME}' created!"