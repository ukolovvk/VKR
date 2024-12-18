import psycopg2
from confluent_kafka import Consumer, KafkaError
from py_eureka_client import eureka_client

eureka_server = "http://localhost:7777/eureka/"
app_name = "ml"
instance_port = 5000

eureka_client.init(
    eureka_server=eureka_server,
    app_name=app_name,
    instance_port=instance_port
)

kafka_config = {
    'bootstrap.servers': 'localhost:9092',
    'group.id': 'my-group',
    'auto.offset.reset': 'earliest'
}

postgres_config = {
    'dbname': 'audio_events',
    'user': 'user',
    'password': 'password',
    'host': 'localhost',
    'port': 5432
}

consumer = Consumer(kafka_config)
consumer.subscribe(['audio'])

def update_audio(uuid):
    try:
        conn = psycopg2.connect(**postgres_config)
        cursor = conn.cursor()
        update_query = "UPDATE audio SET description = %s WHERE s3_uuid = %s"
        cursor.execute(update_query, (f'description for uuid: {uuid}', uuid))
        conn.commit()
        cursor.close()
        conn.close()
    except Exception as e:
        print(f"Failed to update audio db to set description: {e}")

def consume_messages():
    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
                continue
            if msg.error():
                if msg.error().code() == KafkaError._PARTITION_EOF:
                    continue
                else:
                    print(f"Kafka error: {msg.error()}")
                    break
            uuid = msg.value().decode('utf-8')
            print(f"Received UUID: {uuid}")
            update_audio(uuid)
    finally:
        consumer.close()

if __name__ == '__main__':
    consume_messages()