package com.bioproj.kafka;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaMessageViewer {
//    private final KafkaAdmin kafkaAdmin;
//
//    public KafkaMessageViewer(KafkaAdmin kafkaAdmin) {
//        this.kafkaAdmin = kafkaAdmin;
//    }

    public void viewTopicMessages(String topic) {
//        Properties properties = new Properties();
//        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaAdmin.getConfigurationProperties().get(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG));
//
//        try (AdminClient adminClient = AdminClient.create(properties)) {
//            // 获取消费者组列表
//            ListConsumerGroupsResult consumerGroupsResult = adminClient.listConsumerGroups(new ListConsumerGroupsOptions());
//            for (ConsumerGroupListing consumerGroupListing : consumerGroupsResult.all().get()) {
//                String groupId = consumerGroupListing.groupId();
//                // 获取消费者组的描述信息
//                DescribeConsumerGroupsResult consumerGroupsDescription = adminClient.describeConsumerGroups(Collections.singletonList(groupId), new DescribeConsumerGroupsOptions());
//                ConsumerGroupDescription consumerGroupDescription = consumerGroupsDescription.describedGroups().get(groupId).get();
//                // 获取消费者组的消费偏移量
//                ListConsumerGroupOffsetsResult consumerGroupOffsetsResult = adminClient.listConsumerGroupOffsets(groupId, new ListConsumerGroupOffsetsOptions());
//                for (ConsumerGroupOffsetsListing consumerGroupOffsetsListing : consumerGroupOffsetsResult.partitionsToOffsetAndMetadata().get()) {
//                    TopicPartition topicPartition = consumerGroupOffsetsListing.topicPartition();
//                    if (topicPartition.topic().equals(topic)) {
//                        OffsetSpec offsetSpec = OffsetSpec.forTimestamp(System.currentTimeMillis());
//                        long latestOffset = adminClient.listOffsets(Collections.singletonMap(topicPartition, offsetSpec)).values().get(topicPartition).get().offset();
//                        long currentOffset = consumerGroupOffsetsListing.offset();
//                        long messageCount = latestOffset - currentOffset;
//                        System.out.println("Consumer Group: " + groupId);
//                        System.out.println("Topic Partition: " + topicPartition);
//                        System.out.println("Current Offset: " + currentOffset);
//                        System.out.println("Latest Offset: " + latestOffset);
//                        System.out.println("Message Count: " + messageCount);
//
//                        // 可以根据偏移量信息使用 Consumer API 来获取消息内容
//                    }
//                }
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
    }
}
