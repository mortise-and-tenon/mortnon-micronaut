#!/usr/bin/env sh
zone=$(date +"GMT%z")
zone=$(echo $zone | sed 's/00$/:00/g')
export TZ=$zone

/app/applicaiton
