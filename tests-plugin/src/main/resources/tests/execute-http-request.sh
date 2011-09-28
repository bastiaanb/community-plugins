#!/bin/sh
RESPONSE_FILE=${deployed.hostTemporaryDirectoryOrDefault}/http-response.$$
echo Executing "wget <#if (deployed.ignoreCertificateWarnings)>--no-check-certificate</#if> -O $RESPONSE_FILE ${deployed.url}"
wget <#if (deployed.ignoreCertificateWarnings)>--no-check-certificate</#if> -O "$RESPONSE_FILE" ${deployed.url}

WGET_EXIT_CODE=$?
if [ $WGET_EXIT_CODE -ne 0 ]; then
  echo FAILURE: '${deployed.url}' returned non-200 response code
  exit $WGET_EXIT_CODE
fi

grep "${deployed.expectedResponseText}" $RESPONSE_FILE

GREP_EXIT_CODE=$?
rm $RESPONSE_FILE

if [ $GREP_EXIT_CODE -ne 0 ]; then
  echo FAILURE: Response body did not contain "${deployed.expectedResponseText}" but was `cat $RESPONSE_FILE`
  exit $GREP_EXIT_CODE
fi