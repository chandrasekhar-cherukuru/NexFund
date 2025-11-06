package com.chakri.fundly.service;

import com.chakri.fundly.model.Events;
import com.chakri.fundly.repo.EventRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Test Suite")
class EventServiceTest {

    @Mock
    private EventRepo eventRepo;

    @Mock
    private StatsService statsService;

    @InjectMocks
    private EventService eventService;

    private Events testEvent;
    private String testEventId;
    private String testUsername;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testEventId = "test-event-123";
        testUsername = "testuser";

        testEvent = new Events();
        testEvent.setId(testEventId);
        testEvent.setEventTitle("Test Event");
        testEvent.setEventDescription("Test Description");
        testEvent.setUpiId("test@upi");
        testEvent.setUpiMsg("Pay for event");
        testEvent.setEventAmount(1000);
        testEvent.setCreatedByUsername(testUsername);
        testEvent.setImageDataUrl("data:image/png;base64,test");
        testEvent.setParticipants(new ArrayList<>());
    }

    // ==================== getEvents() Tests ====================

    @Test
    @DisplayName("getEvents_WhenEventsExist_ShouldReturnListOfAllEvents")
    void testGetEvents_WhenEventsExist_ShouldReturnListOfAllEvents() {
        List<Events> expectedEvents = new ArrayList<>();
        expectedEvents.add(testEvent);

        Events secondEvent = new Events();
        secondEvent.setId("event-456");
        secondEvent.setEventTitle("Second Event");
        expectedEvents.add(secondEvent);

        when(eventRepo.findAll()).thenReturn(expectedEvents);

        List<Events> actualEvents = eventService.getEvents();

        assertNotNull(actualEvents, "Events list should not be null");
        assertEquals(2, actualEvents.size(), "Should return 2 events");
        assertEquals("Test Event", actualEvents.get(0).getEventTitle(), "First event title should match");
        assertEquals("Second Event", actualEvents.get(1).getEventTitle(), "Second event title should match");
        verify(eventRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("getEvents_WhenNoEventsExist_ShouldReturnEmptyList")
    void testGetEvents_WhenNoEventsExist_ShouldReturnEmptyList() {
        when(eventRepo.findAll()).thenReturn(new ArrayList<>());

        List<Events> actualEvents = eventService.getEvents();

        assertNotNull(actualEvents, "Events list should not be null");
        assertTrue(actualEvents.isEmpty(), "Events list should be empty");
        assertEquals(0, actualEvents.size(), "Size should be 0");
        verify(eventRepo, times(1)).findAll();
    }

    // ==================== saveEvent() Tests ====================

    @Test
    @DisplayName("saveEvent_WithValidEvent_ShouldSaveAndReturnEvent")
    void testSaveEvent_WithValidEvent_ShouldSaveAndReturnEvent() {
        when(eventRepo.save(any(Events.class))).thenReturn(testEvent);

        Events savedEvent = eventService.saveEvent(testEvent);

        assertNotNull(savedEvent, "Saved event should not be null");
        assertEquals(testEventId, savedEvent.getId(), "Event ID should match");
        assertEquals("Test Event", savedEvent.getEventTitle(), "Event title should match");
        verify(eventRepo, times(1)).save(testEvent);
        verify(statsService, times(1)).incrementEventCount();
    }

    @Test
    @DisplayName("saveEvent_WithValidEvent_ShouldIncrementStatsCount")
    void testSaveEvent_WithValidEvent_ShouldIncrementStatsCount() {
        when(eventRepo.save(any(Events.class))).thenReturn(testEvent);

        eventService.saveEvent(testEvent);

        verify(statsService, times(1)).incrementEventCount();
    }

    @Test
    @DisplayName("saveEvent_WithEventHavingAllFields_ShouldPreserveAllFields")
    void testSaveEvent_WithEventHavingAllFields_ShouldPreserveAllFields() {
        testEvent.getParticipants().add("participant1");
        testEvent.getParticipants().add("participant2");

        when(eventRepo.save(any(Events.class))).thenReturn(testEvent);

        Events savedEvent = eventService.saveEvent(testEvent);

        assertEquals(2, savedEvent.getParticipants().size(), "Participants list should be preserved");
        assertEquals("participant1", savedEvent.getParticipants().get(0), "First participant should match");
        assertEquals("test@upi", savedEvent.getUpiId(), "UPI ID should be preserved");
    }

    @Test
    @DisplayName("saveEvent_WithNullEventDescription_ShouldStillSave")
    void testSaveEvent_WithNullEventDescription_ShouldStillSave() {
        testEvent.setEventDescription(null);
        when(eventRepo.save(any(Events.class))).thenReturn(testEvent);

        Events savedEvent = eventService.saveEvent(testEvent);

        assertNull(savedEvent.getEventDescription(), "Event description should be null");
        verify(eventRepo, times(1)).save(testEvent);
    }

    // ==================== getEventById() Tests ====================

    @Test
    @DisplayName("getEventById_WithValidId_ShouldReturnEvent")
    void testGetEventById_WithValidId_ShouldReturnEvent() {
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        Optional<Events> result = eventService.getEventById(testEventId);

        assertTrue(result.isPresent(), "Optional should contain event");
        assertEquals(testEventId, result.get().getId(), "Event ID should match");
        assertEquals("Test Event", result.get().getEventTitle(), "Event title should match");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    @Test
    @DisplayName("getEventById_WithNonExistentId_ShouldReturnEmptyOptional")
    void testGetEventById_WithNonExistentId_ShouldReturnEmptyOptional() {
        when(eventRepo.findById("non-existent")).thenReturn(Optional.empty());

        Optional<Events> result = eventService.getEventById("non-existent");

        assertFalse(result.isPresent(), "Optional should be empty");
        assertTrue(result.isEmpty(), "Optional should be empty");
        verify(eventRepo, times(1)).findById("non-existent");
    }

    @Test
    @DisplayName("getEventById_WithNullId_ShouldReturnEmptyOptional")
    void testGetEventById_WithNullId_ShouldReturnEmptyOptional() {
        when(eventRepo.findById(null)).thenReturn(Optional.empty());

        Optional<Events> result = eventService.getEventById(null);

        assertFalse(result.isPresent(), "Optional should be empty for null ID");
        verify(eventRepo, times(1)).findById(null);
    }

    @Test
    @DisplayName("getEventById_WithEmptyStringId_ShouldReturnEmptyOptional")
    void testGetEventById_WithEmptyStringId_ShouldReturnEmptyOptional() {
        when(eventRepo.findById("")).thenReturn(Optional.empty());

        Optional<Events> result = eventService.getEventById("");

        assertFalse(result.isPresent(), "Optional should be empty for empty string ID");
        verify(eventRepo, times(1)).findById("");
    }

    // ==================== deleteEvent() Tests ====================

    @Test
    @DisplayName("deleteEvent_WithValidId_ShouldCallDeleteById")
    void testDeleteEvent_WithValidId_ShouldCallDeleteById() {
        doNothing().when(eventRepo).deleteById(testEventId);

        eventService.deleteEvent(testEventId);

        verify(eventRepo, times(1)).deleteById(testEventId);
    }

    @Test
    @DisplayName("deleteEvent_WithNullId_ShouldCallDeleteById")
    void testDeleteEvent_WithNullId_ShouldCallDeleteById() {
        doNothing().when(eventRepo).deleteById(null);

        eventService.deleteEvent(null);

        verify(eventRepo, times(1)).deleteById(null);
    }

    @Test
    @DisplayName("deleteEvent_WithEmptyStringId_ShouldCallDeleteById")
    void testDeleteEvent_WithEmptyStringId_ShouldCallDeleteById() {
        doNothing().when(eventRepo).deleteById("");

        eventService.deleteEvent("");

        verify(eventRepo, times(1)).deleteById("");
    }

    // ==================== getUpiId() Tests ====================

    @Test
    @DisplayName("getUpiId_WithValidEventId_ShouldReturnUpiId")
    void testGetUpiId_WithValidEventId_ShouldReturnUpiId() {
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        String upiId = eventService.getUpiId(testEventId);

        assertNotNull(upiId, "UPI ID should not be null");
        assertEquals("test@upi", upiId, "UPI ID should match");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    @Test
    @DisplayName("getUpiId_WithNonExistentId_ShouldReturnNull")
    void testGetUpiId_WithNonExistentId_ShouldReturnNull() {
        when(eventRepo.findById("non-existent")).thenReturn(Optional.empty());

        String upiId = eventService.getUpiId("non-existent");

        assertNull(upiId, "UPI ID should be null for non-existent event");
        verify(eventRepo, times(1)).findById("non-existent");
    }

    @Test
    @DisplayName("getUpiId_WithEventHavingNullUpiId_ShouldReturnNull")
    void testGetUpiId_WithEventHavingNullUpiId_ShouldReturnNull() {
        testEvent.setUpiId(null);
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        String upiId = eventService.getUpiId(testEventId);

        assertNull(upiId, "UPI ID should be null when event UPI ID is null");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    // ==================== getUpiMsg() Tests ====================

    @Test
    @DisplayName("getUpiMsg_WithValidEventId_ShouldReturnUpiMsg")
    void testGetUpiMsg_WithValidEventId_ShouldReturnUpiMsg() {
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        String upiMsg = eventService.getUpiMsg(testEventId);

        assertNotNull(upiMsg, "UPI message should not be null");
        assertEquals("Pay for event", upiMsg, "UPI message should match");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    @Test
    @DisplayName("getUpiMsg_WithNonExistentId_ShouldReturnNull")
    void testGetUpiMsg_WithNonExistentId_ShouldReturnNull() {
        when(eventRepo.findById("non-existent")).thenReturn(Optional.empty());

        String upiMsg = eventService.getUpiMsg("non-existent");

        assertNull(upiMsg, "UPI message should be null for non-existent event");
        verify(eventRepo, times(1)).findById("non-existent");
    }

    @Test
    @DisplayName("getUpiMsg_WithEventHavingNullUpiMsg_ShouldReturnNull")
    void testGetUpiMsg_WithEventHavingNullUpiMsg_ShouldReturnNull() {
        testEvent.setUpiMsg(null);
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        String upiMsg = eventService.getUpiMsg(testEventId);

        assertNull(upiMsg, "UPI message should be null when event UPI message is null");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    // ==================== getUserName() Tests ====================

    @Test
    @DisplayName("getUserName_WithValidEventId_ShouldReturnUsername")
    void testGetUserName_WithValidEventId_ShouldReturnUsername() {
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        String username = eventService.getUserName(testEventId);

        assertNotNull(username, "Username should not be null");
        assertEquals(testUsername, username, "Username should match");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    @Test
    @DisplayName("getUserName_WithNonExistentId_ShouldReturnNull")
    void testGetUserName_WithNonExistentId_ShouldReturnNull() {
        when(eventRepo.findById("non-existent")).thenReturn(Optional.empty());

        String username = eventService.getUserName("non-existent");

        assertNull(username, "Username should be null for non-existent event");
        verify(eventRepo, times(1)).findById("non-existent");
    }

    @Test
    @DisplayName("getUserName_WithEventHavingNullUsername_ShouldReturnNull")
    void testGetUserName_WithEventHavingNullUsername_ShouldReturnNull() {
        testEvent.setCreatedByUsername(null);
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        String username = eventService.getUserName(testEventId);

        assertNull(username, "Username should be null when event username is null");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    // ==================== getEventTitle() Tests ====================

    @Test
    @DisplayName("getEventTitle_WithValidEventId_ShouldReturnEventTitle")
    void testGetEventTitle_WithValidEventId_ShouldReturnEventTitle() {
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        String title = eventService.getEventTitle(testEventId);

        assertNotNull(title, "Event title should not be null");
        assertEquals("Test Event", title, "Event title should match");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    @Test
    @DisplayName("getEventTitle_WithNonExistentId_ShouldReturnNull")
    void testGetEventTitle_WithNonExistentId_ShouldReturnNull() {
        when(eventRepo.findById("non-existent")).thenReturn(Optional.empty());

        String title = eventService.getEventTitle("non-existent");

        assertNull(title, "Event title should be null for non-existent event");
        verify(eventRepo, times(1)).findById("non-existent");
    }

    @Test
    @DisplayName("getEventTitle_WithEventHavingNullTitle_ShouldReturnNull")
    void testGetEventTitle_WithEventHavingNullTitle_ShouldReturnNull() {
        testEvent.setEventTitle(null);
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        String title = eventService.getEventTitle(testEventId);

        assertNull(title, "Event title should be null when event title is null");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    @Test
    @DisplayName("getEventTitle_WithEventHavingEmptyTitle_ShouldReturnEmpty")
    void testGetEventTitle_WithEventHavingEmptyTitle_ShouldReturnEmpty() {
        testEvent.setEventTitle("");
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        String title = eventService.getEventTitle(testEventId);

        assertNotNull(title, "Event title should not be null");
        assertTrue(title.isEmpty(), "Event title should be empty");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    // ==================== getEventDescription() Tests ====================

    @Test
    @DisplayName("getEventDescription_WithValidEventId_ShouldReturnDescription")
    void testGetEventDescription_WithValidEventId_ShouldReturnDescription() {
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        String description = eventService.getEventDescription(testEventId);

        assertNotNull(description, "Event description should not be null");
        assertEquals("Test Description", description, "Event description should match");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    @Test
    @DisplayName("getEventDescription_WithNonExistentId_ShouldReturnNull")
    void testGetEventDescription_WithNonExistentId_ShouldReturnNull() {
        when(eventRepo.findById("non-existent")).thenReturn(Optional.empty());

        String description = eventService.getEventDescription("non-existent");

        assertNull(description, "Event description should be null for non-existent event");
        verify(eventRepo, times(1)).findById("non-existent");
    }

    @Test
    @DisplayName("getEventDescription_WithEventHavingNullDescription_ShouldReturnNull")
    void testGetEventDescription_WithEventHavingNullDescription_ShouldReturnNull() {
        testEvent.setEventDescription(null);
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        String description = eventService.getEventDescription(testEventId);

        assertNull(description, "Event description should be null when event description is null");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    // ==================== getEventAmount() Tests ====================

    @Test
    @DisplayName("getEventAmount_WithValidEventId_ShouldReturnAmount")
    void testGetEventAmount_WithValidEventId_ShouldReturnAmount() {
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        int amount = eventService.getEventAmount(testEventId);

        assertEquals(1000, amount, "Event amount should match");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    @Test
    @DisplayName("getEventAmount_WithNonExistentId_ShouldReturnZero")
    void testGetEventAmount_WithNonExistentId_ShouldReturnZero() {
        when(eventRepo.findById("non-existent")).thenReturn(Optional.empty());

        int amount = eventService.getEventAmount("non-existent");

        assertEquals(0, amount, "Event amount should be 0 for non-existent event");
        verify(eventRepo, times(1)).findById("non-existent");
    }

    @Test
    @DisplayName("getEventAmount_WithZeroAmount_ShouldReturnZero")
    void testGetEventAmount_WithZeroAmount_ShouldReturnZero() {
        testEvent.setEventAmount(0);
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        int amount = eventService.getEventAmount(testEventId);

        assertEquals(0, amount, "Event amount should be 0");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    @Test
    @DisplayName("getEventAmount_WithLargeAmount_ShouldReturnAmount")
    void testGetEventAmount_WithLargeAmount_ShouldReturnAmount() {
        testEvent.setEventAmount(1000000);
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        int amount = eventService.getEventAmount(testEventId);

        assertEquals(1000000, amount, "Event amount should return large amount");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    @Test
    @DisplayName("getEventAmount_WithNegativeAmount_ShouldReturnNegative")
    void testGetEventAmount_WithNegativeAmount_ShouldReturnNegative() {
        testEvent.setEventAmount(-500);
        when(eventRepo.findById(testEventId)).thenReturn(Optional.of(testEvent));

        int amount = eventService.getEventAmount(testEventId);

        assertEquals(-500, amount, "Event amount should return negative amount");
        verify(eventRepo, times(1)).findById(testEventId);
    }

    // ==================== getEventsByCreator() Tests ====================

    @Test
    @DisplayName("getEventsByCreator_WithValidUsername_ShouldReturnUserEvents")
    void testGetEventsByCreator_WithValidUsername_ShouldReturnUserEvents() {
        List<Events> userEvents = new ArrayList<>();
        userEvents.add(testEvent);

        Events secondUserEvent = new Events();
        secondUserEvent.setId("event-789");
        secondUserEvent.setEventTitle("Another Event");
        secondUserEvent.setCreatedByUsername(testUsername);
        userEvents.add(secondUserEvent);

        when(eventRepo.findByCreatedByUsername(testUsername)).thenReturn(userEvents);

        List<Events> result = eventService.getEventsByCreator(testUsername);

        assertNotNull(result, "Events list should not be null");
        assertEquals(2, result.size(), "Should return 2 events for user");
        assertEquals(testUsername, result.get(0).getCreatedByUsername(), "First event creator should match");
        assertEquals(testUsername, result.get(1).getCreatedByUsername(), "Second event creator should match");
        verify(eventRepo, times(1)).findByCreatedByUsername(testUsername);
    }

    @Test
    @DisplayName("getEventsByCreator_WithNonExistentUsername_ShouldReturnEmptyList")
    void testGetEventsByCreator_WithNonExistentUsername_ShouldReturnEmptyList() {
        when(eventRepo.findByCreatedByUsername("unknownuser")).thenReturn(new ArrayList<>());

        List<Events> result = eventService.getEventsByCreator("unknownuser");

        assertNotNull(result, "Events list should not be null");
        assertTrue(result.isEmpty(), "Events list should be empty for unknown user");
        assertEquals(0, result.size(), "Size should be 0");
        verify(eventRepo, times(1)).findByCreatedByUsername("unknownuser");
    }

    @Test
    @DisplayName("getEventsByCreator_WithNullUsername_ShouldReturnEmptyList")
    void testGetEventsByCreator_WithNullUsername_ShouldReturnEmptyList() {
        when(eventRepo.findByCreatedByUsername(null)).thenReturn(new ArrayList<>());

        List<Events> result = eventService.getEventsByCreator(null);

        assertNotNull(result, "Events list should not be null");
        assertTrue(result.isEmpty(), "Events list should be empty for null username");
        verify(eventRepo, times(1)).findByCreatedByUsername(null);
    }

    @Test
    @DisplayName("getEventsByCreator_WithEmptyUsername_ShouldReturnEmptyList")
    void testGetEventsByCreator_WithEmptyUsername_ShouldReturnEmptyList() {
        when(eventRepo.findByCreatedByUsername("")).thenReturn(new ArrayList<>());

        List<Events> result = eventService.getEventsByCreator("");

        assertNotNull(result, "Events list should not be null");
        assertTrue(result.isEmpty(), "Events list should be empty for empty username");
        verify(eventRepo, times(1)).findByCreatedByUsername("");
    }

    @Test
    @DisplayName("getEventsByCreator_WithSingleEventUser_ShouldReturnSingleEvent")
    void testGetEventsByCreator_WithSingleEventUser_ShouldReturnSingleEvent() {
        List<Events> userEvents = new ArrayList<>();
        userEvents.add(testEvent);

        when(eventRepo.findByCreatedByUsername(testUsername)).thenReturn(userEvents);

        List<Events> result = eventService.getEventsByCreator(testUsername);

        assertEquals(1, result.size(), "Should return 1 event");
        assertEquals(testEventId, result.get(0).getId(), "Event ID should match");
        verify(eventRepo, times(1)).findByCreatedByUsername(testUsername);
    }

    @Test
    @DisplayName("getEventsByCreator_WithMultipleUserEvents_ShouldReturnAllEvents")
    void testGetEventsByCreator_WithMultipleUserEvents_ShouldReturnAllEvents() {
        List<Events> userEvents = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Events event = new Events();
            event.setId("event-" + i);
            event.setEventTitle("Event " + i);
            event.setCreatedByUsername(testUsername);
            userEvents.add(event);
        }

        when(eventRepo.findByCreatedByUsername(testUsername)).thenReturn(userEvents);

        List<Events> result = eventService.getEventsByCreator(testUsername);

        assertEquals(5, result.size(), "Should return all 5 events");
        for (int i = 0; i < 5; i++) {
            assertEquals(testUsername, result.get(i).getCreatedByUsername(), "Creator should match for event " + i);
        }
        verify(eventRepo, times(1)).findByCreatedByUsername(testUsername);
    }
}
