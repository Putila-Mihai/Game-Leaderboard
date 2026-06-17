package personal.leaderboard.config;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import leaderboard.PlayerScoreEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.Map;

@Configuration
@EnableKafkaStreams
@DependsOn({"rawScoresTopic", "computedScoresTopic"})
public class KafkaStreamsConfig {

    private static final String RAW_SCORES_TOPIC = "leaderboard.raw-scores";
    private static final String COMPUTED_SCORES_TOPIC = "leaderboard.computed-scores";


    @Bean
    public KStream<String, PlayerScoreEvent> stream(final StreamsBuilder builder){
        KStream<String, PlayerScoreEvent> rawScores = builder.stream(RAW_SCORES_TOPIC, Consumed.with(Serdes.String(),getAvroSerde()));

        rawScores
                .selectKey((originalKey, event) ->
                    event.getPlayerId() + ":" + event.getGameId())
                .groupByKey(Grouped.with(Serdes.String(), getAvroSerde()))
                .reduce((existingEvent, newEvent) -> {
                    if (newEvent.getScore() > existingEvent.getScore()) {
                        return newEvent;
                    }
                    return existingEvent;
                })
                .toStream()
                .to(COMPUTED_SCORES_TOPIC, Produced.with(Serdes.String(), getAvroSerde()));

        return rawScores;
    }


    private SpecificAvroSerde<PlayerScoreEvent> getAvroSerde() {
        SpecificAvroSerde<PlayerScoreEvent> serde = new SpecificAvroSerde<>();
        serde.configure(Map.of(
                AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                "http://localhost:18081"
        ), false);
        return serde;
    }
}
