package com.prosilion.superconductor;

import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.GenericTagQuery;
import nostr.event.BaseMessage;
import nostr.event.filter.Filters;
import nostr.event.filter.GenericTagQueryFilter;
import nostr.event.filter.GeohashTagFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.json.codec.BaseMessageDecoder;
import nostr.event.message.EoseMessage;
import nostr.event.message.EventMessage;
import nostr.event.message.ReqMessage;
import nostr.event.tag.GeohashTag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.EventMessageIT.getGenericEvents;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingGeohashTagQueryIT {
  private final NostrRelayService nostrRelayService;
  private final static String subscriberId = Factory.generateRandomHex64String();

  @Autowired
  MatchingGeohashTagQueryIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_geohash_tag_query_filter_input.txt"))) {
      String textMessageEventJson = lines.collect(Collectors.joining("\n"));
      log.debug("setup() send event:\n  {}", textMessageEventJson);
      assertTrue(nostrRelayService.send(
              new BaseMessageDecoder<EventMessage>().decode(textMessageEventJson))
          .getFlag());
    }
  }

  @Test
  void testReqMessagesNoGenericMatch() throws IOException, ExecutionException, InterruptedException {
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String genericTagString = "textnote-geo-tag-non-existent";

    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(new GenericTagQueryFilter<>(
            new GenericTagQuery("#g", genericTagString))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertEquals(1, returnedBaseMessages.size());
    List<EoseMessage> eoseMessageStream = returnedBaseMessages
        .stream()
        .filter(EoseMessage.class::isInstance)
        .map(EoseMessage.class::cast).toList();
    assertEquals(1, eoseMessageStream.size());
  }

  @Test
  void testReqMessagesMatchesGeneric() throws IOException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String geohashTagString = "textnote-geo-tag-1";
    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(new GenericTagQueryFilter<>(
            new GenericTagQuery("#g", geohashTagString))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEvent> returnedEvents = getGenericEvents(returnedBaseMessages);

    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    //    associated event
    assertTrue(returnedEvents.stream().anyMatch(s -> s.getId().equals("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e590004")));
    assertTrue(returnedEvents.stream().map(event ->
        event.getTags().stream().anyMatch(s -> s.toString().equals(geohashTagString))).findAny().isPresent());
    assertTrue(returnedBaseMessages.stream().anyMatch(EoseMessage.class::isInstance));
  }

  @Test
  void testReqMessagesMatchesGeoHashTag() throws IOException {
    String subscriberId = Factory.generateRandomHex64String();
    //    TODO: impl another test containing a space in string, aka "textnote geo-tag-1"
    String geohashTagString = "textnote-geo-tag-1";
    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(new GeohashTagFilter<>(
            new GeohashTag(geohashTagString))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEvent> returnedEvents = getGenericEvents(returnedBaseMessages);

    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    //    associated event
    assertTrue(returnedEvents.stream().anyMatch(s -> s.getId().equals("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e590004")));
    assertTrue(returnedEvents.stream().map(event ->
        event.getTags().stream().anyMatch(s -> s.toString().equals(geohashTagString))).findAny().isPresent());
    assertTrue(returnedBaseMessages.stream().anyMatch(EoseMessage.class::isInstance));
  }
}
