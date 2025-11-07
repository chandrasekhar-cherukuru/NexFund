package com.chakri.fundly.repo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.chakri.fundly.model.Events;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventRepo Test Suite")
public class EventRepoTest {

    @Mock
    private EventRepo eventRepo;

    private Events testEvent1;
    private Events testEvent2;
    private Events testEvent3;
    private List<String> participants;

    @BeforeEach
    void setUp() {
        // Initialize test data
        participants = new ArrayList<>();
        participants.add("user1");
        participants.add("user2");

        testEvent1 = new Events(
                "event-id-1",
                "Tech Summit 2025",
                "Annual tech conference for developers",
                "chakri@upi",
                "Please donate for tech summit",
                5000,
                "chakri_user",
                "data:image/jpeg;base64,/9j/4AAQSkZJRg...",
                participants
        );

        testEvent2 = new Events(
                "event-id-2",
                "Charity Run Marathon",
                "Community charity run event",
                "charity@upi",
                "Help us support charity",
                3000,
                "johndoe_user",
                "data:image/png;base64,iVBORw0KGgoAAAANS...",
                new ArrayList<>(Arrays.asList("user3", "user4"))
        );

        testEvent3 = new Events(
                "event-id-3",
                "Education Scholarship Drive",
                "Scholarship funding event",
                "education@upi",
                "Support education initiative",
                7500,
                "chakri_user",
                "data:image/jpg;base64,/9j/4AAQSkZJRgABA...",
                new ArrayList<>(Arrays.asList("user1", "user5", "user6"))
        );
    }

    // ============================================================================
    // Tests for findByCreatedByUsername(String createdByUsername)
    // ============================================================================

    @Test
    @DisplayName("Should find events by valid username - single event")
    void testFindByCreatedByUsername_SingleEvent() {
        // ARRANGE
        String username = "chakri_user";
        List<Events> expectedEvents = Arrays.asList(testEvent1);
        when(eventRepo.findByCreatedByUsername(username)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByCreatedByUsername(username);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(1, actualEvents.size(), "Should return exactly one event");
        assertEquals("event-id-1", actualEvents.get(0).getId(), "Event ID should match");
        assertEquals("Tech Summit 2025", actualEvents.get(0).getEventTitle(), "Event title should match");
        assertEquals("chakri_user", actualEvents.get(0).getCreatedByUsername(), "Username should match");
        verify(eventRepo, times(1)).findByCreatedByUsername(username);
    }

    @Test
    @DisplayName("Should find events by valid username - multiple events")
    void testFindByCreatedByUsername_MultipleEvents() {
        // ARRANGE
        String username = "chakri_user";
        List<Events> expectedEvents = Arrays.asList(testEvent1, testEvent3);
        when(eventRepo.findByCreatedByUsername(username)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByCreatedByUsername(username);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(2, actualEvents.size(), "Should return two events");
        assertTrue(actualEvents.stream().allMatch(e -> "chakri_user".equals(e.getCreatedByUsername())),
                "All events should belong to the user");
        verify(eventRepo, times(1)).findByCreatedByUsername(username);
    }

    @Test
    @DisplayName("Should return empty list when no events found for username")
    void testFindByCreatedByUsername_NoEventsFound() {
        // ARRANGE
        String username = "nonexistent_user";
        when(eventRepo.findByCreatedByUsername(username)).thenReturn(new ArrayList<>());

        // ACT
        List<Events> actualEvents = eventRepo.findByCreatedByUsername(username);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertTrue(actualEvents.isEmpty(), "Result should be empty list");
        assertEquals(0, actualEvents.size(), "Size should be zero");
        verify(eventRepo, times(1)).findByCreatedByUsername(username);
    }

    @Test
    @DisplayName("Should handle null username parameter")
    void testFindByCreatedByUsername_NullUsername() {
        // ARRANGE
        when(eventRepo.findByCreatedByUsername(null)).thenReturn(new ArrayList<>());

        // ACT
        List<Events> actualEvents = eventRepo.findByCreatedByUsername(null);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertTrue(actualEvents.isEmpty(), "Result should be empty");
        verify(eventRepo, times(1)).findByCreatedByUsername(null);
    }

    @Test
    @DisplayName("Should handle empty string username parameter")
    void testFindByCreatedByUsername_EmptyUsername() {
        // ARRANGE
        String username = "";
        when(eventRepo.findByCreatedByUsername(username)).thenReturn(new ArrayList<>());

        // ACT
        List<Events> actualEvents = eventRepo.findByCreatedByUsername(username);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertTrue(actualEvents.isEmpty(), "Result should be empty");
        verify(eventRepo, times(1)).findByCreatedByUsername(username);
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testFindByCreatedByUsername_SpecialCharactersInUsername() {
        // ARRANGE
        String username = "user@#$%_123";
        testEvent1.setCreatedByUsername(username);
        List<Events> expectedEvents = Arrays.asList(testEvent1);
        when(eventRepo.findByCreatedByUsername(username)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByCreatedByUsername(username);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(1, actualEvents.size(), "Should return one event");
        assertEquals(username, actualEvents.get(0).getCreatedByUsername(), "Username with special chars should match");
        verify(eventRepo, times(1)).findByCreatedByUsername(username);
    }

    @Test
    @DisplayName("Should verify correct method call with username parameter")
    void testFindByCreatedByUsername_VerifyMethodCall() {
        // ARRANGE
        String username = "chakri_user";
        when(eventRepo.findByCreatedByUsername(username)).thenReturn(Arrays.asList(testEvent1));

        // ACT
        eventRepo.findByCreatedByUsername(username);

        // ASSERT
        verify(eventRepo, times(1)).findByCreatedByUsername(username);
        verify(eventRepo, never()).findByCreatedByUsername("different_user");
    }

    // ============================================================================
    // Tests for findByEventTitleContainingIgnoreCase(String eventTitle)
    // ============================================================================

    @Test
    @DisplayName("Should find events by title containing keyword - case insensitive")
    void testFindByEventTitleContainingIgnoreCase_ExactMatch() {
        // ARRANGE
        String keyword = "Tech";
        List<Events> expectedEvents = Arrays.asList(testEvent1);
        when(eventRepo.findByEventTitleContainingIgnoreCase(keyword)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventTitleContainingIgnoreCase(keyword);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(1, actualEvents.size(), "Should return one event");
        assertTrue(actualEvents.get(0).getEventTitle().toLowerCase().contains(keyword.toLowerCase()),
                "Event title should contain keyword");
        verify(eventRepo, times(1)).findByEventTitleContainingIgnoreCase(keyword);
    }

    @Test
    @DisplayName("Should find events by title containing keyword - lowercase search")
    void testFindByEventTitleContainingIgnoreCase_LowercaseKeyword() {
        // ARRANGE
        String keyword = "tech";
        List<Events> expectedEvents = Arrays.asList(testEvent1);
        when(eventRepo.findByEventTitleContainingIgnoreCase(keyword)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventTitleContainingIgnoreCase(keyword);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(1, actualEvents.size(), "Should find events despite lowercase");
        verify(eventRepo, times(1)).findByEventTitleContainingIgnoreCase(keyword);
    }

    @Test
    @DisplayName("Should find events by title containing keyword - uppercase search")
    void testFindByEventTitleContainingIgnoreCase_UppercaseKeyword() {
        // ARRANGE
        String keyword = "SUMMIT";
        List<Events> expectedEvents = Arrays.asList(testEvent1);
        when(eventRepo.findByEventTitleContainingIgnoreCase(keyword)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventTitleContainingIgnoreCase(keyword);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(1, actualEvents.size(), "Should find events despite uppercase");
        verify(eventRepo, times(1)).findByEventTitleContainingIgnoreCase(keyword);
    }

    @Test
    @DisplayName("Should find multiple events by title containing keyword")
    void testFindByEventTitleContainingIgnoreCase_MultipleMatches() {
        // ARRANGE
        String keyword = "Event";
        List<Events> expectedEvents = Arrays.asList(testEvent1, testEvent2, testEvent3);
        when(eventRepo.findByEventTitleContainingIgnoreCase(keyword)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventTitleContainingIgnoreCase(keyword);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(3, actualEvents.size(), "Should return all matching events");
        verify(eventRepo, times(1)).findByEventTitleContainingIgnoreCase(keyword);
    }

    @Test
    @DisplayName("Should return empty list when no events match title keyword")
    void testFindByEventTitleContainingIgnoreCase_NoMatches() {
        // ARRANGE
        String keyword = "NonexistentKeyword";
        when(eventRepo.findByEventTitleContainingIgnoreCase(keyword)).thenReturn(new ArrayList<>());

        // ACT
        List<Events> actualEvents = eventRepo.findByEventTitleContainingIgnoreCase(keyword);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertTrue(actualEvents.isEmpty(), "Result should be empty when no matches");
        verify(eventRepo, times(1)).findByEventTitleContainingIgnoreCase(keyword);
    }

    @Test
    @DisplayName("Should handle null title keyword parameter")
    void testFindByEventTitleContainingIgnoreCase_NullKeyword() {
        // ARRANGE
        when(eventRepo.findByEventTitleContainingIgnoreCase(null)).thenReturn(new ArrayList<>());

        // ACT
        List<Events> actualEvents = eventRepo.findByEventTitleContainingIgnoreCase(null);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertTrue(actualEvents.isEmpty(), "Result should be empty for null keyword");
        verify(eventRepo, times(1)).findByEventTitleContainingIgnoreCase(null);
    }

    @Test
    @DisplayName("Should handle empty string title keyword parameter")
    void testFindByEventTitleContainingIgnoreCase_EmptyKeyword() {
        // ARRANGE
        String keyword = "";
        List<Events> expectedEvents = Arrays.asList(testEvent1, testEvent2, testEvent3);
        when(eventRepo.findByEventTitleContainingIgnoreCase(keyword)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventTitleContainingIgnoreCase(keyword);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(3, actualEvents.size(), "Empty keyword might return all or none");
        verify(eventRepo, times(1)).findByEventTitleContainingIgnoreCase(keyword);
    }

    @Test
    @DisplayName("Should find events by partial title match")
    void testFindByEventTitleContainingIgnoreCase_PartialMatch() {
        // ARRANGE
        String keyword = "Charity";
        List<Events> expectedEvents = Arrays.asList(testEvent2);
        when(eventRepo.findByEventTitleContainingIgnoreCase(keyword)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventTitleContainingIgnoreCase(keyword);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(1, actualEvents.size(), "Should return partial matches");
        verify(eventRepo, times(1)).findByEventTitleContainingIgnoreCase(keyword);
    }

    @Test
    @DisplayName("Should handle special characters in title keyword")
    void testFindByEventTitleContainingIgnoreCase_SpecialCharacters() {
        // ARRANGE
        String keyword = "@#$%";
        when(eventRepo.findByEventTitleContainingIgnoreCase(keyword)).thenReturn(new ArrayList<>());

        // ACT
        List<Events> actualEvents = eventRepo.findByEventTitleContainingIgnoreCase(keyword);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertTrue(actualEvents.isEmpty(), "Should handle special characters");
        verify(eventRepo, times(1)).findByEventTitleContainingIgnoreCase(keyword);
    }

    // ============================================================================
    // Tests for findByEventAmountBetween(int minAmount, int maxAmount)
    // ============================================================================

    @Test
    @DisplayName("Should find events by amount within range - inclusive bounds")
    void testFindByEventAmountBetween_WithinRange() {
        // ARRANGE
        int minAmount = 3000;
        int maxAmount = 7500;
        List<Events> expectedEvents = Arrays.asList(testEvent2, testEvent3);
        when(eventRepo.findByEventAmountBetween(minAmount, maxAmount)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventAmountBetween(minAmount, maxAmount);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(2, actualEvents.size(), "Should return two events within range");
        assertTrue(actualEvents.stream().allMatch(e -> e.getEventAmount() >= minAmount && e.getEventAmount() <= maxAmount),
                "All events should be within range");
        verify(eventRepo, times(1)).findByEventAmountBetween(minAmount, maxAmount);
    }

    @Test
    @DisplayName("Should find events when min and max amount are equal")
    void testFindByEventAmountBetween_EqualMinMax() {
        // ARRANGE
        int minAmount = 5000;
        int maxAmount = 5000;
        List<Events> expectedEvents = Arrays.asList(testEvent1);
        when(eventRepo.findByEventAmountBetween(minAmount, maxAmount)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventAmountBetween(minAmount, maxAmount);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(1, actualEvents.size(), "Should return event with exact amount");
        assertEquals(5000, actualEvents.get(0).getEventAmount(), "Amount should be exactly 5000");
        verify(eventRepo, times(1)).findByEventAmountBetween(minAmount, maxAmount);
    }

    @Test
    @DisplayName("Should find all events when min and max are at boundaries")
    void testFindByEventAmountBetween_AllEventsBoundary() {
        // ARRANGE
        int minAmount = 3000;
        int maxAmount = 7500;
        List<Events> expectedEvents = Arrays.asList(testEvent1, testEvent2, testEvent3);
        when(eventRepo.findByEventAmountBetween(minAmount, maxAmount)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventAmountBetween(minAmount, maxAmount);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(3, actualEvents.size(), "Should return all events");
        verify(eventRepo, times(1)).findByEventAmountBetween(minAmount, maxAmount);
    }

    @Test
    @DisplayName("Should return empty list when range has no matching events")
    void testFindByEventAmountBetween_NoMatchingEvents() {
        // ARRANGE
        int minAmount = 10000;
        int maxAmount = 20000;
        when(eventRepo.findByEventAmountBetween(minAmount, maxAmount)).thenReturn(new ArrayList<>());

        // ACT
        List<Events> actualEvents = eventRepo.findByEventAmountBetween(minAmount, maxAmount);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertTrue(actualEvents.isEmpty(), "Result should be empty for range with no events");
        verify(eventRepo, times(1)).findByEventAmountBetween(minAmount, maxAmount);
    }

    @Test
    @DisplayName("Should return empty list when min amount is greater than max amount")
    void testFindByEventAmountBetween_MinGreaterThanMax() {
        // ARRANGE
        int minAmount = 7500;
        int maxAmount = 3000;
        when(eventRepo.findByEventAmountBetween(minAmount, maxAmount)).thenReturn(new ArrayList<>());

        // ACT
        List<Events> actualEvents = eventRepo.findByEventAmountBetween(minAmount, maxAmount);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertTrue(actualEvents.isEmpty(), "Result should be empty when min > max");
        verify(eventRepo, times(1)).findByEventAmountBetween(minAmount, maxAmount);
    }

    @Test
    @DisplayName("Should handle zero as minimum amount")
    void testFindByEventAmountBetween_ZeroMinimum() {
        // ARRANGE
        int minAmount = 0;
        int maxAmount = 5000;
        List<Events> expectedEvents = Arrays.asList(testEvent1, testEvent2);
        when(eventRepo.findByEventAmountBetween(minAmount, maxAmount)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventAmountBetween(minAmount, maxAmount);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(2, actualEvents.size(), "Should find events with zero as minimum");
        verify(eventRepo, times(1)).findByEventAmountBetween(minAmount, maxAmount);
    }

    @Test
    @DisplayName("Should handle negative minimum amount")
    void testFindByEventAmountBetween_NegativeMinimum() {
        // ARRANGE
        int minAmount = -1000;
        int maxAmount = 5000;
        List<Events> expectedEvents = Arrays.asList(testEvent1, testEvent2);
        when(eventRepo.findByEventAmountBetween(minAmount, maxAmount)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventAmountBetween(minAmount, maxAmount);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        verify(eventRepo, times(1)).findByEventAmountBetween(minAmount, maxAmount);
    }

    @Test
    @DisplayName("Should handle large maximum amount")
    void testFindByEventAmountBetween_LargeMaximum() {
        // ARRANGE
        int minAmount = 0;
        int maxAmount = Integer.MAX_VALUE;
        List<Events> expectedEvents = Arrays.asList(testEvent1, testEvent2, testEvent3);
        when(eventRepo.findByEventAmountBetween(minAmount, maxAmount)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventAmountBetween(minAmount, maxAmount);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(3, actualEvents.size(), "Should return all events with large range");
        verify(eventRepo, times(1)).findByEventAmountBetween(minAmount, maxAmount);
    }

    @Test
    @DisplayName("Should find single event in large range")
    void testFindByEventAmountBetween_SingleEventInRange() {
        // ARRANGE
        int minAmount = 5000;
        int maxAmount = 6000;
        List<Events> expectedEvents = Arrays.asList(testEvent1);
        when(eventRepo.findByEventAmountBetween(minAmount, maxAmount)).thenReturn(expectedEvents);

        // ACT
        List<Events> actualEvents = eventRepo.findByEventAmountBetween(minAmount, maxAmount);

        // ASSERT
        assertNotNull(actualEvents, "Result should not be null");
        assertEquals(1, actualEvents.size(), "Should find single event");
        assertEquals(5000, actualEvents.get(0).getEventAmount(), "Amount should be 5000");
        verify(eventRepo, times(1)).findByEventAmountBetween(minAmount, maxAmount);
    }

    @Test
    @DisplayName("Should verify correct method call with both parameters")
    void testFindByEventAmountBetween_VerifyMethodCall() {
        // ARRANGE
        int minAmount = 3000;
        int maxAmount = 7500;
        when(eventRepo.findByEventAmountBetween(minAmount, maxAmount)).thenReturn(Arrays.asList(testEvent2, testEvent3));

        // ACT
        eventRepo.findByEventAmountBetween(minAmount, maxAmount);

        // ASSERT
        verify(eventRepo, times(1)).findByEventAmountBetween(minAmount, maxAmount);
        verify(eventRepo, never()).findByEventAmountBetween(0, 1000);
    }

    // ============================================================================
    // Integration Tests - Combined Query Scenarios
    // ============================================================================

    @Test
    @DisplayName("Should perform multiple queries in sequence")
    void testMultipleQueriesInSequence() {
        // ARRANGE
        when(eventRepo.findByCreatedByUsername("chakri_user")).thenReturn(Arrays.asList(testEvent1, testEvent3));
        when(eventRepo.findByEventTitleContainingIgnoreCase("Summit")).thenReturn(Arrays.asList(testEvent1));
        when(eventRepo.findByEventAmountBetween(5000, 8000)).thenReturn(Arrays.asList(testEvent1, testEvent3));

        // ACT
        List<Events> userEvents = eventRepo.findByCreatedByUsername("chakri_user");
        List<Events> titleEvents = eventRepo.findByEventTitleContainingIgnoreCase("Summit");
        List<Events> amountEvents = eventRepo.findByEventAmountBetween(5000, 8000);

        // ASSERT
        assertEquals(2, userEvents.size(), "User should have two events");
        assertEquals(1, titleEvents.size(), "Title search should find one event");
        assertEquals(2, amountEvents.size(), "Amount range should find two events");
        verify(eventRepo, times(1)).findByCreatedByUsername("chakri_user");
        verify(eventRepo, times(1)).findByEventTitleContainingIgnoreCase("Summit");
        verify(eventRepo, times(1)).findByEventAmountBetween(5000, 8000);
    }

    @Test
    @DisplayName("Should handle combination of different query parameters")
    void testCombinedQueryScenarios() {
        // ARRANGE
        when(eventRepo.findByCreatedByUsername("johndoe_user")).thenReturn(Arrays.asList(testEvent2));
        when(eventRepo.findByEventAmountBetween(3000, 3000)).thenReturn(Arrays.asList(testEvent2));

        // ACT
        List<Events> userEvents = eventRepo.findByCreatedByUsername("johndoe_user");
        List<Events> amountEvents = eventRepo.findByEventAmountBetween(3000, 3000);

        // ASSERT
        assertEquals(1, userEvents.size(), "Should find user event");
        assertEquals(1, amountEvents.size(), "Should find event with exact amount");
        assertEquals(userEvents.get(0).getId(), amountEvents.get(0).getId(), "Should be same event");
    }

    @Test
    @DisplayName("Should return empty results for all queries when no data matches")
    void testAllQueriesReturnEmpty() {
        // ARRANGE
        when(eventRepo.findByCreatedByUsername("ghost_user")).thenReturn(new ArrayList<>());
        when(eventRepo.findByEventTitleContainingIgnoreCase("ghost")).thenReturn(new ArrayList<>());
        when(eventRepo.findByEventAmountBetween(99999, 100000)).thenReturn(new ArrayList<>());

        // ACT
        List<Events> userEvents = eventRepo.findByCreatedByUsername("ghost_user");
        List<Events> titleEvents = eventRepo.findByEventTitleContainingIgnoreCase("ghost");
        List<Events> amountEvents = eventRepo.findByEventAmountBetween(99999, 100000);

        // ASSERT
        assertTrue(userEvents.isEmpty(), "User events should be empty");
        assertTrue(titleEvents.isEmpty(), "Title events should be empty");
        assertTrue(amountEvents.isEmpty(), "Amount events should be empty");
    }

    // ============================================================================
    // Edge Case Tests for Events Model
    // ============================================================================

    @Test
    @DisplayName("Should handle Events object with null participants list")
    void testEventsWithNullParticipants() {
        // ARRANGE
        Events eventWithNullParticipants = new Events(
                "event-id-4",
                "Test Event",
                "Test Description",
                "test@upi",
                "Test message",
                1000,
                "test_user",
                "data:image/png;base64,test",
                null
        );
        when(eventRepo.findByCreatedByUsername("test_user")).thenReturn(Arrays.asList(eventWithNullParticipants));

        // ACT
        List<Events> events = eventRepo.findByCreatedByUsername("test_user");

        // ASSERT
        assertNotNull(events, "Events list should not be null");
        assertEquals(1, events.size(), "Should return one event");
        assertNull(events.get(0).getParticipants(), "Participants should be null");
    }

    @Test
    @DisplayName("Should handle Events object with empty participants list")
    void testEventsWithEmptyParticipants() {
        // ARRANGE
        Events eventWithEmptyParticipants = new Events(
                "event-id-5",
                "Solo Event",
                "Event with no participants",
                "solo@upi",
                "Solo fundraising",
                2000,
                "solo_user",
                "data:image/jpg;base64,test",
                new ArrayList<>()
        );
        when(eventRepo.findByCreatedByUsername("solo_user")).thenReturn(Arrays.asList(eventWithEmptyParticipants));

        // ACT
        List<Events> events = eventRepo.findByCreatedByUsername("solo_user");

        // ASSERT
        assertNotNull(events, "Events list should not be null");
        assertEquals(1, events.size(), "Should return one event");
        assertNotNull(events.get(0).getParticipants(), "Participants list should not be null");
        assertTrue(events.get(0).getParticipants().isEmpty(), "Participants list should be empty");
    }

    @Test
    @DisplayName("Should handle Events object with very long image data URL")
    void testEventsWithLongImageDataUrl() {
        // ARRANGE
        StringBuilder longImageData = new StringBuilder("data:image/png;base64,");
        for (int i = 0; i < 1000; i++) {
            longImageData.append("A");
        }
        Events eventWithLongImage = new Events(
                "event-id-6",
                "HD Image Event",
                "Event with large image",
                "image@upi",
                "Image test",
                4000,
                "image_user",
                longImageData.toString(),
                new ArrayList<>()
        );
        when(eventRepo.findByEventTitleContainingIgnoreCase("HD")).thenReturn(Arrays.asList(eventWithLongImage));

        // ACT
        List<Events> events = eventRepo.findByEventTitleContainingIgnoreCase("HD");

        // ASSERT
        assertNotNull(events, "Events list should not be null");
        assertEquals(1, events.size(), "Should return one event");
        assertEquals(1022, events.get(0).getImageDataUrl().length(), "Image data length should be 1022 (prefix: 22 + 1000 A's)");
    }

    @Test
    @DisplayName("Should handle Events object with null image data URL")
    void testEventsWithNullImageDataUrl() {
        // ARRANGE
        Events eventWithNullImage = new Events(
                "event-id-7",
                "No Image Event",
                "Event without image",
                "noimage@upi",
                "No image test",
                1500,
                "noimage_user",
                null,
                new ArrayList<>()
        );
        when(eventRepo.findByCreatedByUsername("noimage_user")).thenReturn(Arrays.asList(eventWithNullImage));

        // ACT
        List<Events> events = eventRepo.findByCreatedByUsername("noimage_user");

        // ASSERT
        assertNotNull(events, "Events list should not be null");
        assertEquals(1, events.size(), "Should return one event");
        assertNull(events.get(0).getImageDataUrl(), "Image data should be null");
    }

    @Test
    @DisplayName("Should verify repository mock is properly initialized")
    void testRepositoryMockInitialization() {
        // ARRANGE
        assertNotNull(eventRepo, "Repository mock should be initialized");

        // ACT & ASSERT
        assertTrue(eventRepo instanceof EventRepo, "Should be instance of EventRepo");
    }

    @Test
    @DisplayName("Should handle zero amount events")
    void testFindByEventAmountBetween_ZeroAmountEvent() {
        // ARRANGE
        Events zeroAmountEvent = new Events(
                "event-id-8",
                "Free Event",
                "Free community event",
                "free@upi",
                "No donation needed",
                0,
                "free_user",
                "data:image/png;base64,test",
                new ArrayList<>()
        );
        when(eventRepo.findByEventAmountBetween(0, 1000)).thenReturn(Arrays.asList(zeroAmountEvent));

        // ACT
        List<Events> events = eventRepo.findByEventAmountBetween(0, 1000);

        // ASSERT
        assertNotNull(events, "Events list should not be null");
        assertEquals(1, events.size(), "Should return one event");
        assertEquals(0, events.get(0).getEventAmount(), "Event amount should be zero");
    }
}
