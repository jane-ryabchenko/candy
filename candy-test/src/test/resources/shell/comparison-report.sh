#!/usr/bin/env bash

# Create new report and copy id from terminal (it should look like 9f53c964-b377-436a-8b4d-20fe7527305e)
curl -i -X POST http://localhost:8080/reports \
    -H 'Content-Type: application/json' \
    -d '{"failedComparisons":"1","totalComparisons":"1"}' && echo

# Get existing report (replace <REPLACE_WITH_COPIED_ID> with copied id)
curl 'http://localhost:8080/reports/<REPLACE_WITH_COPIED_ID>'

# Get existing report (replace <REPLACE_WITH_COPIED_ID> with copied id)
curl -i -X POST 'http://localhost:8080/reports/<REPLACE_WITH_COPIED_ID>/comparisons' \
    -H 'Content-type:multipart/mixed' \
    -F 'comparison={"comparisonNumber":"0","name":"testCaptureScreenshot_capturedBodyScreenshot","diffPercentage":"21.1","actualFileName":"candy-test/target/candy-actual/testCaptureScreenshot_capturedBodyScreenshot.png","originFileName":"candy-test/src/test/resources/candy-origin/testCaptureScreenshot_capturedBodyScreenshot.png"};type=application/json' \
    -F 'actual=@candy-test/target/candy-actual/testCaptureScreenshot_capturedBodyScreenshotWithExclusion.png;type=image/png' \
    -F 'origin=@candy-test/target/candy-actual/testCaptureScreenshot_capturedBodyScreenshot.png'
