package com.chakri.fundly.controller;

import com.chakri.fundly.model.Events;
import com.chakri.fundly.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventController Test Suite")
class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private Events testEvent;
    private List<Events> testEventList;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testEvent = new Events();
        testEvent.setId("event-123");
        testEvent.setEventTitle("Tech Conference 2025");
        testEvent.setEventDescription("Annual tech conference");
        testEvent.setUpiId("user@upi");
        testEvent.setUpiMsg("Please contribute");
        testEvent.setEventAmount(1000);
        testEvent.setCreatedByUsername("john_doe");
        testEvent.setImageDataUrl("data:image/png;base64,");
        testEvent.setParticipants(new ArrayList<>());

        testEventList = new ArrayList<>();
        testEventList.add(testEvent);
    }

    // ============= GREET METHOD TESTS =============

    @Test
    @DisplayName("Should return greeting message successfully")
    void testGreet_Success() {
        // Arrange - No mocks needed for this simple method
        String expectedGreeting = "Hello World";

        // Act
        String actualGreeting = eventController.greet();

        // Assert
        assertEquals(expectedGreeting, actualGreeting);
        assertNotNull(actualGreeting);
        assertFalse(actualGreeting.isEmpty());
    }

    // ============= GET ALL EVENTS METHOD TESTS =============

    @Test
    @DisplayName("Should retrieve all events successfully")
    void testGetEvents_Success() {
        // Arrange
        when(eventService.getEvents()).thenReturn(testEventList);

        // Act
        List<Events> result = eventController.getEvents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("event-123", result.get(0).getId());
        assertEquals("Tech Conference 2025", result.get(0).getEventTitle());
        verify(eventService, times(1)).getEvents();
    }

    @Test
    @DisplayName("Should return empty list when no events exist")
    void testGetEvents_EmptyList() {
        // Arrange
        when(eventService.getEvents()).thenReturn(new ArrayList<>());

        // Act
        List<Events> result = eventController.getEvents();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(eventService, times(1)).getEvents();
    }

    @Test
    @DisplayName("Should handle null response from service")
    void testGetEvents_NullResponse() {
        // Arrange
        when(eventService.getEvents()).thenReturn(null);

        // Act
        List<Events> result = eventController.getEvents();

        // Assert
        assertNull(result);
        verify(eventService, times(1)).getEvents();
    }

    @Test
    @DisplayName("Should return multiple events successfully")
    void testGetEvents_MultipleEvents() {
        // Arrange
        Events event2 = new Events();
        event2.setId("event-456");
        event2.setEventTitle("WebDev Summit");
        event2.setCreatedByUsername("jane_doe");

        testEventList.add(event2);
        when(eventService.getEvents()).thenReturn(testEventList);

        // Act
        List<Events> result = eventController.getEvents();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tech Conference 2025", result.get(0).getEventTitle());
        assertEquals("WebDev Summit", result.get(1).getEventTitle());
        verify(eventService, times(1)).getEvents();
    }

    // ============= CREATE EVENT METHOD TESTS =============

    @Test
    @DisplayName("Should create event successfully with valid data")
    void testCreateEvent_Success() {
        // Arrange
        Events newEvent = new Events();
        newEvent.setEventTitle("New Event");
        newEvent.setEventDescription("Description");
        newEvent.setUpiId("user@upi");
        newEvent.setEventAmount(500);
        newEvent.setCreatedByUsername("user_123");

        Events savedEvent = new Events();
        savedEvent.setId("event-789");
        savedEvent.setEventTitle("New Event");
        savedEvent.setEventDescription("Description");
        savedEvent.setUpiId("user@upi");
        savedEvent.setEventAmount(500);
        savedEvent.setCreatedByUsername("user_123");

        when(eventService.saveEvent(newEvent)).thenReturn(savedEvent);

        // Act
        ResponseEntity<Events> response = eventController.createEvent(newEvent);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("event-789", response.getBody().getId());
        assertEquals("New Event", response.getBody().getEventTitle());
        assertEquals(500, response.getBody().getEventAmount());
        verify(eventService, times(1)).saveEvent(newEvent);
    }

    @Test
    @DisplayName("Should save event with all fields populated")
    void testCreateEvent_AllFieldsPopulated() {
        // Arrange
        Events completeEvent = new Events();
        completeEvent.setEventTitle("Complete Event");
        completeEvent.setEventDescription("Full description");
        completeEvent.setUpiId("complete@upi");
        completeEvent.setUpiMsg("Complete message");
        completeEvent.setEventAmount(2000);
        completeEvent.setCreatedByUsername("complete_user");
        completeEvent.setImageDataUrl("data:image/jpeg;base64,abc123");
        completeEvent.setParticipants(List.of("participant1", "participant2"));

        Events savedCompleteEvent = new Events();
        savedCompleteEvent.setId("event-complete-123");
        savedCompleteEvent.setEventTitle("Complete Event");
        savedCompleteEvent.setEventDescription("Full description");
        savedCompleteEvent.setUpiId("complete@upi");
        savedCompleteEvent.setUpiMsg("Complete message");
        savedCompleteEvent.setEventAmount(2000);
        savedCompleteEvent.setCreatedByUsername("complete_user");
        savedCompleteEvent.setImageDataUrl("data:image/jpeg;base64,abc123");
        savedCompleteEvent.setParticipants(List.of("participant1", "participant2"));

        when(eventService.saveEvent(completeEvent)).thenReturn(savedCompleteEvent);

        // Act
        ResponseEntity<Events> response = eventController.createEvent(completeEvent);

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Complete Event", response.getBody().getEventTitle());
        assertEquals(2, response.getBody().getParticipants().size());
        assertEquals("data:image/jpeg;base64,abc123", response.getBody().getImageDataUrl());
        verify(eventService, times(1)).saveEvent(completeEvent);
    }

    @Test
    @DisplayName("Should create event with minimum required fields")
    void testCreateEvent_MinimumFields() {
        // Arrange
        Events minimalEvent = new Events();
        minimalEvent.setEventTitle("Minimal Event");
        minimalEvent.setEventAmount(100);

        Events savedMinimalEvent = new Events();
        savedMinimalEvent.setId("event-minimal");
        savedMinimalEvent.setEventTitle("Minimal Event");
        savedMinimalEvent.setEventAmount(100);

        when(eventService.saveEvent(minimalEvent)).thenReturn(savedMinimalEvent);

        // Act
        ResponseEntity<Events> response = eventController.createEvent(minimalEvent);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Minimal Event", response.getBody().getEventTitle());
        assertEquals(100, response.getBody().getEventAmount());
        verify(eventService, times(1)).saveEvent(minimalEvent);
    }

    @Test
    @DisplayName("Should verify correct event is passed to service")
    void testCreateEvent_VerifyServiceCall() {
        // Arrange
        Events eventToSave = new Events();
        eventToSave.setEventTitle("Service Test Event");
        eventToSave.setEventAmount(750);

        when(eventService.saveEvent(eventToSave)).thenReturn(testEvent);

        // Act
        eventController.createEvent(eventToSave);

        // Assert - Verify the exact object was passed
        ArgumentCaptor<Events> argumentCaptor = ArgumentCaptor.forClass(Events.class);
        verify(eventService, times(1)).saveEvent(argumentCaptor.capture());
        Events capturedEvent = argumentCaptor.getValue();
        assertEquals("Service Test Event", capturedEvent.getEventTitle());
        assertEquals(750, capturedEvent.getEventAmount());
    }

    // ============= GET EVENT BY ID METHOD TESTS =============

    @Test
    @DisplayName("Should retrieve event by valid ID successfully")
    void testGetEventById_Success() {
        // Arrange
        String eventId = "event-123";
        when(eventService.getEventById(eventId)).thenReturn(Optional.of(testEvent));

        // Act
        ResponseEntity<Events> response = eventController.getEventById(eventId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("event-123", response.getBody().getId());
        assertEquals("Tech Conference 2025", response.getBody().getEventTitle());
        verify(eventService, times(1)).getEventById(eventId);
    }

    @Test
    @DisplayName("Should return 404 when event ID not found")
    void testGetEventById_NotFound() {
        // Arrange
        String eventId = "non-existent-id";
        when(eventService.getEventById(eventId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Events> response = eventController.getEventById(eventId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(eventService, times(1)).getEventById(eventId);
    }

    @Test
    @DisplayName("Should handle null ID parameter gracefully")
    void testGetEventById_NullId() {
        // Arrange
        when(eventService.getEventById(null)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Events> response = eventController.getEventById(null);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(eventService, times(1)).getEventById(null);
    }

    @Test
    @DisplayName("Should handle empty string ID")
    void testGetEventById_EmptyId() {
        // Arrange
        String emptyId = "";
        when(eventService.getEventById(emptyId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Events> response = eventController.getEventById(emptyId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(eventService, times(1)).getEventById(emptyId);
    }

    @Test
    @DisplayName("Should retrieve event with complete data")
    void testGetEventById_FullEventData() {
        // Arrange
        Events completeEvent = new Events();
        completeEvent.setId("full-event-123");
        completeEvent.setEventTitle("Full Event");
        completeEvent.setEventDescription("Complete description");
        completeEvent.setUpiId("full@upi");
        completeEvent.setUpiMsg("Full message");
        completeEvent.setEventAmount(5000);
        completeEvent.setCreatedByUsername("full_user");
        completeEvent.setImageDataUrl("data:image/png;base64,full");
        completeEvent.setParticipants(List.of("p1", "p2", "p3"));

        when(eventService.getEventById("full-event-123")).thenReturn(Optional.of(completeEvent));

        // Act
        ResponseEntity<Events> response = eventController.getEventById("full-event-123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getParticipants().size());
        assertEquals(5000, response.getBody().getEventAmount());
        verify(eventService, times(1)).getEventById("full-event-123");
    }

    // ============= DELETE EVENT METHOD TESTS =============

    @Test
    @DisplayName("Should delete event successfully")
    void testDeleteEvent_Success() {
        // Arrange
        String eventId = "event-123";
        doNothing().when(eventService).deleteEvent(eventId);

        // Act
        ResponseEntity<Void> response = eventController.deleteEvent(eventId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(eventService, times(1)).deleteEvent(eventId);
    }

    @Test
    @DisplayName("Should handle deletion of non-existent event")
    void testDeleteEvent_NonExistent() {
        // Arrange
        String eventId = "non-existent-id";
        doNothing().when(eventService).deleteEvent(eventId);

        // Act
        ResponseEntity<Void> response = eventController.deleteEvent(eventId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eventService, times(1)).deleteEvent(eventId);
    }

    @Test
    @DisplayName("Should handle null ID for deletion")
    void testDeleteEvent_NullId() {
        // Arrange
        doNothing().when(eventService).deleteEvent(null);

        // Act
        ResponseEntity<Void> response = eventController.deleteEvent(null);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eventService, times(1)).deleteEvent(null);
    }

    @Test
    @DisplayName("Should handle empty string ID for deletion")
    void testDeleteEvent_EmptyId() {
        // Arrange
        String emptyId = "";
        doNothing().when(eventService).deleteEvent(emptyId);

        // Act
        ResponseEntity<Void> response = eventController.deleteEvent(emptyId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(eventService, times(1)).deleteEvent(emptyId);
    }

    @Test
    @DisplayName("Should verify correct ID is passed to service for deletion")
    void testDeleteEvent_VerifyServiceCall() {
        // Arrange
        String eventId = "verify-delete-123";
        doNothing().when(eventService).deleteEvent(eventId);

        // Act
        eventController.deleteEvent(eventId);

        // Assert
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventService, times(1)).deleteEvent(idCaptor.capture());
        assertEquals("verify-delete-123", idCaptor.getValue());
    }

    // ============= SEND EMAIL METHOD TESTS =============

    @Test
    @DisplayName("Should send email successfully")
    void testSendEmail_Success() {
        // Arrange
        Events eventForEmail = new Events();
        eventForEmail.setEventTitle("Email Event");
        eventForEmail.setUpiMsg("Send email");

        // Act
        ResponseEntity<Void> response = eventController.sendEmail(eventForEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Should handle email with null event")
    void testSendEmail_NullEvent() {
        // Act & Assert - The method should handle null gracefully
        assertDoesNotThrow(() -> eventController.sendEmail(null));
    }

    @Test
    @DisplayName("Should send email with complete event data")
    void testSendEmail_CompleteEventData() {
        // Arrange
        Events completeEventForEmail = new Events();
        completeEventForEmail.setId("email-event-123");
        completeEventForEmail.setEventTitle("Email Event Title");
        completeEventForEmail.setEventDescription("Email Event Description");
        completeEventForEmail.setUpiId("email@upi");
        completeEventForEmail.setUpiMsg("Please send email");
        completeEventForEmail.setEventAmount(1500);
        completeEventForEmail.setCreatedByUsername("email_user");

        // Act
        ResponseEntity<Void> response = eventController.sendEmail(completeEventForEmail);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should send email with minimal event data")
    void testSendEmail_MinimalEventData() {
        // Arrange
        Events minimalEventForEmail = new Events();
        minimalEventForEmail.setEventTitle("Minimal Email Event");

        // Act
        ResponseEntity<Void> response = eventController.sendEmail(minimalEventForEmail);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ============= GET EVENTS BY CREATOR METHOD TESTS =============

    @Test
    @DisplayName("Should retrieve events by creator successfully")
    void testGetEventsByCreator_Success() {
        // Arrange
        String username = "john_doe";
        List<Events> creatorEvents = new ArrayList<>();
        creatorEvents.add(testEvent);

        when(eventService.getEventsByCreator(username)).thenReturn(creatorEvents);

        // Act
        ResponseEntity<List<Events>> response = eventController.getEventsByCreator(username);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("john_doe", response.getBody().get(0).getCreatedByUsername());
        verify(eventService, times(1)).getEventsByCreator(username);
    }

    @Test
    @DisplayName("Should return empty list when creator has no events")
    void testGetEventsByCreator_NoEvents() {
        // Arrange
        String username = "new_user";
        when(eventService.getEventsByCreator(username)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<Events>> response = eventController.getEventsByCreator(username);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(eventService, times(1)).getEventsByCreator(username);
    }

    @Test
    @DisplayName("Should retrieve multiple events for same creator")
    void testGetEventsByCreator_MultipleEvents() {
        // Arrange
        String username = "john_doe";
        Events event1 = new Events();
        event1.setId("event-1");
        event1.setEventTitle("Event 1");
        event1.setCreatedByUsername(username);

        Events event2 = new Events();
        event2.setId("event-2");
        event2.setEventTitle("Event 2");
        event2.setCreatedByUsername(username);

        Events event3 = new Events();
        event3.setId("event-3");
        event3.setEventTitle("Event 3");
        event3.setCreatedByUsername(username);

        List<Events> creatorEvents = List.of(event1, event2, event3);
        when(eventService.getEventsByCreator(username)).thenReturn(creatorEvents);

        // Act
        ResponseEntity<List<Events>> response = eventController.getEventsByCreator(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
        assertTrue(response.getBody().stream().allMatch(e -> e.getCreatedByUsername().equals(username)));
        verify(eventService, times(1)).getEventsByCreator(username);
    }

    @Test
    @DisplayName("Should handle null username parameter")
    void testGetEventsByCreator_NullUsername() {
        // Arrange
        when(eventService.getEventsByCreator(null)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<Events>> response = eventController.getEventsByCreator(null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(eventService, times(1)).getEventsByCreator(null);
    }

    @Test
    @DisplayName("Should handle empty string username")
    void testGetEventsByCreator_EmptyUsername() {
        // Arrange
        String emptyUsername = "";
        when(eventService.getEventsByCreator(emptyUsername)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<Events>> response = eventController.getEventsByCreator(emptyUsername);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(eventService, times(1)).getEventsByCreator(emptyUsername);
    }

    @Test
    @DisplayName("Should handle username with special characters")
    void testGetEventsByCreator_SpecialCharacters() {
        // Arrange
        String specialUsername = "user@domain.com";
        Events specialEvent = new Events();
        specialEvent.setId("special-event");
        specialEvent.setEventTitle("Special Event");
        specialEvent.setCreatedByUsername(specialUsername);

        List<Events> events = List.of(specialEvent);
        when(eventService.getEventsByCreator(specialUsername)).thenReturn(events);

        // Act
        ResponseEntity<List<Events>> response = eventController.getEventsByCreator(specialUsername);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(specialUsername, response.getBody().get(0).getCreatedByUsername());
        verify(eventService, times(1)).getEventsByCreator(specialUsername);
    }

    @Test
    @DisplayName("Should verify correct username is passed to service")
    void testGetEventsByCreator_VerifyServiceCall() {
        // Arrange
        String usernameToVerify = "verify_user";
        when(eventService.getEventsByCreator(usernameToVerify)).thenReturn(testEventList);

        // Act
        eventController.getEventsByCreator(usernameToVerify);

        // Assert
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        verify(eventService, times(1)).getEventsByCreator(usernameCaptor.capture());
        assertEquals("verify_user", usernameCaptor.getValue());
    }

    @Test
    @DisplayName("Should handle null response from service")
    void testGetEventsByCreator_NullResponse() {
        // Arrange
        String username = "any_user";
        when(eventService.getEventsByCreator(username)).thenReturn(null);

        // Act
        ResponseEntity<List<Events>> response = eventController.getEventsByCreator(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(eventService, times(1)).getEventsByCreator(username);
    }

    // ============= EDGE CASES AND INTEGRATION TESTS =============

    @Test
    @DisplayName("Should handle concurrent service calls independently")
    void testConcurrentServiceCalls() {
        // Arrange
        when(eventService.getEvents()).thenReturn(testEventList);
        when(eventService.getEventById("event-123")).thenReturn(Optional.of(testEvent));

        // Act
        List<Events> allEvents = eventController.getEvents();
        ResponseEntity<Events> singleEvent = eventController.getEventById("event-123");

        // Assert
        assertNotNull(allEvents);
        assertNotNull(singleEvent);
        assertEquals(1, allEvents.size());
        assertEquals(HttpStatus.OK, singleEvent.getStatusCode());
        verify(eventService, times(1)).getEvents();
        verify(eventService, times(1)).getEventById("event-123");
    }

    @Test
    @DisplayName("Should handle ResponseEntity status codes correctly")
    void testResponseEntityStatusCodes() {
        // Arrange
        when(eventService.saveEvent(testEvent)).thenReturn(testEvent);
        when(eventService.getEventById("event-123")).thenReturn(Optional.of(testEvent));

        // Act
        ResponseEntity<Events> createResponse = eventController.createEvent(testEvent);
        ResponseEntity<Events> getResponse = eventController.getEventById("event-123");
        ResponseEntity<Void> deleteResponse = eventController.deleteEvent("event-123");

        // Assert
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }

    @Test
    @DisplayName("Should verify mock interactions were called exact number of times")
    void testMockInteractionCounts() {
        // Arrange
        when(eventService.getEvents()).thenReturn(testEventList);

        // Act
        eventController.getEvents();
        eventController.getEvents();
        eventController.getEvents();

        // Assert
        verify(eventService, times(3)).getEvents();
        verify(eventService, never()).saveEvent(any());
        verify(eventService, never()).deleteEvent(anyString());
    }
}
