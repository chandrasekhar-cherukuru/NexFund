package com.chakri.fundly.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Events Entity Unit Tests")
class EventsTest {

    // No @InjectMocks needed here as Events is a plain entity without dependencies
    // We'll instantiate it directly since it's a POJO entity

    private Events events;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize a fresh Events instance for each test
        events = new Events();
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Should create Events with default constructor")
    void testDefaultConstructor() {
        // Arrange & Act
        Events newEvents = new Events();

        // Assert
        assertNotNull(newEvents);
        assertNull(newEvents.getId());
        assertNull(newEvents.getEventTitle());
        assertNull(newEvents.getEventDescription());
        assertNull(newEvents.getUpiId());
        assertNull(newEvents.getUpiMsg());
        assertEquals(0, newEvents.getEventAmount());
        assertNull(newEvents.getCreatedByUsername());
        assertNull(newEvents.getImageDataUrl());
        assertNotNull(newEvents.getParticipants());
        assertTrue(newEvents.getParticipants().isEmpty());
    }

    @Test
    @DisplayName("Should create Events with parameterized constructor")
    void testParameterizedConstructor() {
        // Arrange
        String id = "event-123";
        String eventTitle = "Annual Fundraiser";
        String eventDescription = "Charity event for education";
        String upiId = "upi@bank";
        String upiMsg = "Please contribute";
        int eventAmount = 5000;
        String createdByUsername = "admin";
        String imageDataUrl = "data:image/png;base64,ABC123";
        List<String> participants = new ArrayList<>();
        participants.add("user1");
        participants.add("user2");

        // Act
        Events createdEvent = new Events(
                id,
                eventTitle,
                eventDescription,
                upiId,
                upiMsg,
                eventAmount,
                createdByUsername,
                imageDataUrl,
                participants
        );

        // Assert
        assertNotNull(createdEvent);
        assertEquals(id, createdEvent.getId());
        assertEquals(eventTitle, createdEvent.getEventTitle());
        assertEquals(eventDescription, createdEvent.getEventDescription());
        assertEquals(upiId, createdEvent.getUpiId());
        assertEquals(upiMsg, createdEvent.getUpiMsg());
        assertEquals(eventAmount, createdEvent.getEventAmount());
        assertEquals(createdByUsername, createdEvent.getCreatedByUsername());
        assertEquals(imageDataUrl, createdEvent.getImageDataUrl());
        assertEquals(2, createdEvent.getParticipants().size());
        assertTrue(createdEvent.getParticipants().contains("user1"));
    }

    // ==================== Setter & Getter Tests ====================

    @Test
    @DisplayName("Should set and get id correctly")
    void testSetAndGetId() {
        // Arrange
        String testId = "event-456";

        // Act
        events.setId(testId);

        // Assert
        assertEquals(testId, events.getId());
    }

    @Test
    @DisplayName("Should handle null id")
    void testSetAndGetIdWithNull() {
        // Arrange & Act
        events.setId(null);

        // Assert
        assertNull(events.getId());
    }

    @Test
    @DisplayName("Should set and get eventTitle correctly")
    void testSetAndGetEventTitle() {
        // Arrange
        String testTitle = "Tech Innovation Summit";

        // Act
        events.setEventTitle(testTitle);

        // Assert
        assertEquals(testTitle, events.getEventTitle());
    }

    @Test
    @DisplayName("Should handle empty eventTitle string")
    void testSetAndGetEventTitleEmpty() {
        // Arrange & Act
        events.setEventTitle("");

        // Assert
        assertEquals("", events.getEventTitle());
    }

    @Test
    @DisplayName("Should handle null eventTitle")
    void testSetAndGetEventTitleNull() {
        // Arrange & Act
        events.setEventTitle(null);

        // Assert
        assertNull(events.getEventTitle());
    }

    @Test
    @DisplayName("Should set and get eventDescription correctly")
    void testSetAndGetEventDescription() {
        // Arrange
        String testDescription = "A comprehensive tech event for innovators";

        // Act
        events.setEventDescription(testDescription);

        // Assert
        assertEquals(testDescription, events.getEventDescription());
    }

    @Test
    @DisplayName("Should handle null eventDescription")
    void testSetAndGetEventDescriptionNull() {
        // Arrange & Act
        events.setEventDescription(null);

        // Assert
        assertNull(events.getEventDescription());
    }

    @Test
    @DisplayName("Should set and get upiId correctly")
    void testSetAndGetUpiId() {
        // Arrange
        String testUpiId = "fundraiser@hdfc";

        // Act
        events.setUpiId(testUpiId);

        // Assert
        assertEquals(testUpiId, events.getUpiId());
    }

    @Test
    @DisplayName("Should handle null upiId")
    void testSetAndGetUpiIdNull() {
        // Arrange & Act
        events.setUpiId(null);

        // Assert
        assertNull(events.getUpiId());
    }

    @Test
    @DisplayName("Should set and get upiMsg correctly")
    void testSetAndGetUpiMsg() {
        // Arrange
        String testUpiMsg = "Help us make a difference today!";

        // Act
        events.setUpiMsg(testUpiMsg);

        // Assert
        assertEquals(testUpiMsg, events.getUpiMsg());
    }

    @Test
    @DisplayName("Should handle null upiMsg")
    void testSetAndGetUpiMsgNull() {
        // Arrange & Act
        events.setUpiMsg(null);

        // Assert
        assertNull(events.getUpiMsg());
    }

    @Test
    @DisplayName("Should set and get eventAmount correctly")
    void testSetAndGetEventAmount() {
        // Arrange
        int testAmount = 10000;

        // Act
        events.setEventAmount(testAmount);

        // Assert
        assertEquals(testAmount, events.getEventAmount());
    }

    @Test
    @DisplayName("Should handle zero eventAmount")
    void testSetAndGetEventAmountZero() {
        // Arrange & Act
        events.setEventAmount(0);

        // Assert
        assertEquals(0, events.getEventAmount());
    }

    @Test
    @DisplayName("Should handle negative eventAmount")
    void testSetAndGetEventAmountNegative() {
        // Arrange & Act
        events.setEventAmount(-5000);

        // Assert
        assertEquals(-5000, events.getEventAmount());
    }

    @Test
    @DisplayName("Should handle large eventAmount")
    void testSetAndGetEventAmountLarge() {
        // Arrange
        int largeAmount = Integer.MAX_VALUE;

        // Act
        events.setEventAmount(largeAmount);

        // Assert
        assertEquals(largeAmount, events.getEventAmount());
    }

    @Test
    @DisplayName("Should set and get createdByUsername correctly")
    void testSetAndGetCreatedByUsername() {
        // Arrange
        String testUsername = "admin_user";

        // Act
        events.setCreatedByUsername(testUsername);

        // Assert
        assertEquals(testUsername, events.getCreatedByUsername());
    }

    @Test
    @DisplayName("Should handle null createdByUsername")
    void testSetAndGetCreatedByUsernameNull() {
        // Arrange & Act
        events.setCreatedByUsername(null);

        // Assert
        assertNull(events.getCreatedByUsername());
    }

    @Test
    @DisplayName("Should set and get imageDataUrl correctly")
    void testSetAndGetImageDataUrl() {
        // Arrange
        String testImageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABA";

        // Act
        events.setImageDataUrl(testImageUrl);

        // Assert
        assertEquals(testImageUrl, events.getImageDataUrl());
    }

    @Test
    @DisplayName("Should handle null imageDataUrl")
    void testSetAndGetImageDataUrlNull() {
        // Arrange & Act
        events.setImageDataUrl(null);

        // Assert
        assertNull(events.getImageDataUrl());
    }

    @Test
    @DisplayName("Should handle large imageDataUrl (LONGTEXT)")
    void testSetAndGetImageDataUrlLarge() {
        // Arrange
        StringBuilder largeImageData = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            largeImageData.append("a");
        }
        String testImageUrl = largeImageData.toString();

        // Act
        events.setImageDataUrl(testImageUrl);

        // Assert
        assertEquals(testImageUrl, events.getImageDataUrl());
        assertEquals(100000, events.getImageDataUrl().length());
    }

    // ==================== Participants List Tests ====================

    @Test
    @DisplayName("Should initialize participants with empty ArrayList")
    void testParticipantsInitializationEmpty() {
        // Arrange & Act
        Events newEvent = new Events();

        // Assert
        assertNotNull(newEvent.getParticipants());
        assertTrue(newEvent.getParticipants().isEmpty());
        assertEquals(0, newEvent.getParticipants().size());
    }

    @Test
    @DisplayName("Should add single participant")
    void testAddSingleParticipant() {
        // Arrange
        String participant = "user123";

        // Act
        events.getParticipants().add(participant);

        // Assert
        assertEquals(1, events.getParticipants().size());
        assertTrue(events.getParticipants().contains(participant));
    }

    @Test
    @DisplayName("Should add multiple participants")
    void testAddMultipleParticipants() {
        // Arrange
        List<String> participantsList = new ArrayList<>();
        participantsList.add("user1");
        participantsList.add("user2");
        participantsList.add("user3");

        // Act
        events.getParticipants().addAll(participantsList);

        // Assert
        assertEquals(3, events.getParticipants().size());
        assertTrue(events.getParticipants().containsAll(participantsList));
    }

    @Test
    @DisplayName("Should remove participant from list")
    void testRemoveParticipant() {
        // Arrange
        String participant = "user_to_remove";
        events.getParticipants().add(participant);
        events.getParticipants().add("user_keep");

        // Act
        boolean removed = events.getParticipants().remove(participant);

        // Assert
        assertTrue(removed);
        assertEquals(1, events.getParticipants().size());
        assertFalse(events.getParticipants().contains(participant));
        assertTrue(events.getParticipants().contains("user_keep"));
    }

    @Test
    @DisplayName("Should clear all participants")
    void testClearParticipants() {
        // Arrange
        events.getParticipants().add("user1");
        events.getParticipants().add("user2");
        assertEquals(2, events.getParticipants().size());

        // Act
        events.getParticipants().clear();

        // Assert
        assertTrue(events.getParticipants().isEmpty());
        assertEquals(0, events.getParticipants().size());
    }

    @Test
    @DisplayName("Should handle duplicate participants")
    void testDuplicateParticipants() {
        // Arrange
        String participant = "user_duplicate";

        // Act
        events.getParticipants().add(participant);
        events.getParticipants().add(participant);

        // Assert
        assertEquals(2, events.getParticipants().size()); // Lists allow duplicates
        assertTrue(events.getParticipants().contains(participant));
    }

    @Test
    @DisplayName("Should set participants list completely")
    void testSetParticipantsList() {
        // Arrange
        List<String> newParticipantsList = new ArrayList<>();
        newParticipantsList.add("new_user1");
        newParticipantsList.add("new_user2");

        // Act
        events.setParticipants(newParticipantsList);

        // Assert
        assertEquals(newParticipantsList, events.getParticipants());
        assertEquals(2, events.getParticipants().size());
        assertTrue(events.getParticipants().contains("new_user1"));
    }

    @Test
    @DisplayName("Should handle null participants list assignment")
    void testSetParticipantsNull() {
        // Arrange & Act
        events.setParticipants(null);

        // Assert
        assertNull(events.getParticipants());
    }

    // ==================== Lombok Generated Methods Tests ====================

    @Test
    @DisplayName("Should generate toString method correctly")
    void testToString() {
        // Arrange
        events.setId("event-1");
        events.setEventTitle("Test Event");
        events.setEventAmount(5000);

        // Act
        String toString = events.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("Events"));
        assertTrue(toString.contains("event-1") || toString.contains("id"));
    }

    @Test
    @DisplayName("Should generate equals method correctly - same values")
    void testEqualsWithSameValues() {
        // Arrange
        Events event1 = new Events();
        event1.setId("event-1");
        event1.setEventTitle("Test Event");
        event1.setEventAmount(5000);

        Events event2 = new Events();
        event2.setId("event-1");
        event2.setEventTitle("Test Event");
        event2.setEventAmount(5000);

        // Act & Assert
        assertEquals(event1, event2);
    }

    @Test
    @DisplayName("Should generate equals method correctly - different values")
    void testEqualsWithDifferentValues() {
        // Arrange
        Events event1 = new Events();
        event1.setId("event-1");
        event1.setEventTitle("Test Event 1");

        Events event2 = new Events();
        event2.setId("event-2");
        event2.setEventTitle("Test Event 2");

        // Act & Assert
        assertNotEquals(event1, event2);
    }

    @Test
    @DisplayName("Should generate equals method - same instance")
    void testEqualsWithSameInstance() {
        // Arrange & Act
        boolean areEqual = events.equals(events);

        // Assert
        assertTrue(areEqual);
    }

    @Test
    @DisplayName("Should generate equals method - null comparison")
    void testEqualsWithNull() {
        // Arrange & Act
        boolean areEqual = events.equals(null);

        // Assert
        assertFalse(areEqual);
    }

    @Test
    @DisplayName("Should generate equals method - different class comparison")
    void testEqualsWithDifferentClass() {
        // Arrange & Act
        boolean areEqual = events.equals("not_an_event");

        // Assert
        assertFalse(areEqual);
    }

    @Test
    @DisplayName("Should generate hashCode correctly")
    void testHashCode() {
        // Arrange
        Events event1 = new Events();
        event1.setId("event-1");
        event1.setEventTitle("Test Event");

        Events event2 = new Events();
        event2.setId("event-1");
        event2.setEventTitle("Test Event");

        // Act & Assert
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    @DisplayName("Should generate different hashCode for different objects")
    void testHashCodeDifferent() {
        // Arrange
        Events event1 = new Events();
        event1.setId("event-1");

        Events event2 = new Events();
        event2.setId("event-2");

        // Act & Assert
        assertNotEquals(event1.hashCode(), event2.hashCode());
    }

    // ==================== Complex Scenarios & Edge Cases ====================

    @Test
    @DisplayName("Should handle all null fields")
    void testAllNullFields() {
        // Arrange & Act
        Events nullEvent = new Events();
        nullEvent.setId(null);
        nullEvent.setEventTitle(null);
        nullEvent.setEventDescription(null);
        nullEvent.setUpiId(null);
        nullEvent.setUpiMsg(null);
        nullEvent.setCreatedByUsername(null);
        nullEvent.setImageDataUrl(null);
        nullEvent.setParticipants(null);

        // Assert
        assertNull(nullEvent.getId());
        assertNull(nullEvent.getEventTitle());
        assertNull(nullEvent.getEventDescription());
        assertNull(nullEvent.getUpiId());
        assertNull(nullEvent.getUpiMsg());
        assertNull(nullEvent.getCreatedByUsername());
        assertNull(nullEvent.getImageDataUrl());
        assertNull(nullEvent.getParticipants());
    }

    @Test
    @DisplayName("Should handle all fields with valid data")
    void testAllFieldsWithValidData() {
        // Arrange
        String id = "evt-001";
        String title = "Annual Charity Gala";
        String description = "Join us for an evening of giving back";
        String upiId = "charity@icici";
        String msg = "Your contribution counts!";
        int amount = 50000;
        String username = "event_organizer";
        String imageUrl = "data:image/png;base64,iVBORw0KGgo";
        List<String> participants = new ArrayList<>();
        participants.add("donor1");
        participants.add("donor2");

        // Act
        events.setId(id);
        events.setEventTitle(title);
        events.setEventDescription(description);
        events.setUpiId(upiId);
        events.setUpiMsg(msg);
        events.setEventAmount(amount);
        events.setCreatedByUsername(username);
        events.setImageDataUrl(imageUrl);
        events.setParticipants(participants);

        // Assert
        assertEquals(id, events.getId());
        assertEquals(title, events.getEventTitle());
        assertEquals(description, events.getEventDescription());
        assertEquals(upiId, events.getUpiId());
        assertEquals(msg, events.getUpiMsg());
        assertEquals(amount, events.getEventAmount());
        assertEquals(username, events.getCreatedByUsername());
        assertEquals(imageUrl, events.getImageDataUrl());
        assertEquals(2, events.getParticipants().size());
    }

    @Test
    @DisplayName("Should handle special characters in string fields")
    void testSpecialCharactersInStringFields() {
        // Arrange
        String specialTitle = "Event@#$%&*()_+-=[]{}|;:',.<>?";
        String specialDesc = "पर्व-Événement-活動!@#$%";
        String specialUsername = "user_@domain.com";

        // Act
        events.setEventTitle(specialTitle);
        events.setEventDescription(specialDesc);
        events.setCreatedByUsername(specialUsername);

        // Assert
        assertEquals(specialTitle, events.getEventTitle());
        assertEquals(specialDesc, events.getEventDescription());
        assertEquals(specialUsername, events.getCreatedByUsername());
    }

    @Test
    @DisplayName("Should handle very long strings within fields")
    void testVeryLongStrings() {
        // Arrange
        StringBuilder longTitle = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            longTitle.append("A");
        }
        String veryLongTitle = longTitle.toString();

        // Act
        events.setEventTitle(veryLongTitle);

        // Assert
        assertEquals(veryLongTitle, events.getEventTitle());
        assertEquals(255, events.getEventTitle().length());
    }

    @Test
    @DisplayName("Should handle sequential modifications")
    void testSequentialModifications() {
        // Arrange & Act
        events.setId("evt-001");
        events.setEventTitle("Original Title");
        events.setEventAmount(1000);

        // Modify
        events.setId("evt-002");
        events.setEventTitle("Modified Title");
        events.setEventAmount(2000);

        // Assert
        assertEquals("evt-002", events.getId());
        assertEquals("Modified Title", events.getEventTitle());
        assertEquals(2000, events.getEventAmount());
    }

    @Test
    @DisplayName("Should preserve participants list reference after modifications")
    void testParticipantsListReferencePreservation() {
        // Arrange
        List<String> participants = events.getParticipants();
        participants.add("user1");

        // Act
        List<String> participantsRef = events.getParticipants();

        // Assert
        assertSame(participants, participantsRef);
        assertEquals(1, participantsRef.size());
        assertTrue(participantsRef.contains("user1"));
    }
}
