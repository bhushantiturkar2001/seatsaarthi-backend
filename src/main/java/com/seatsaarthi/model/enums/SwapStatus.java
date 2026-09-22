package com.seatsaarthi.model.enums;

/**
 * Lifecycle states of a P2P Seat Swap Request.
 */
public enum SwapStatus {
    LISTED,     // Seat is published on the swap marketplace
    MATCHED,    // Bidirectional match found by the Stream Auto-Matcher engine
    ACCEPTED,   // Proposal accepted, ready for atomic execution
    COMPLETED,  // Atomic swap successfully executed and audited
    CANCELLED,  // Cancelled by passenger
    EXPIRED;    // Train departed or auto-expired

    /**
     * Finite state machine validator to guarantee valid state transitions.
     */
    public boolean canTransitionTo(SwapStatus newStatus) {
        return switch (this) {
            case LISTED -> (newStatus == MATCHED || newStatus == ACCEPTED || newStatus == CANCELLED || newStatus == EXPIRED);
            case MATCHED -> (newStatus == ACCEPTED || newStatus == LISTED || newStatus == CANCELLED);
            case ACCEPTED -> (newStatus == COMPLETED || newStatus == CANCELLED);
            case COMPLETED, CANCELLED, EXPIRED -> false; // Terminal states
        };
    }
}
