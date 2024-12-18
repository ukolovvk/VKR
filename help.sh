#!/bin/bash

kafka-topics.sh --create --topic audio --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1

pip install confluent-kafka psycopg2 -i https://pypi.org/simple