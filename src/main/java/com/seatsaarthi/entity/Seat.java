package com.seatsaarthi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seatsaarthi.model.enums.BerthType;
import jakarta.persistence.*;

import java.util.Objects;

/**
 * Represents an individual physical seat/berth inside a railway coach.
 * Encapsulates berth classification, occupancy state, and compartment bay
 * calculations.
 */
@Entity
@Table(name = "seats", uniqueConstraints = {
		// Seat number (e.g. 45) must be unique WITHIN the same coach
		@UniqueConstraint(name = "uk_seat_number_per_coach", columnNames = { "seat_number", "coach_id" }) }, indexes = {
				@Index(name = "idx_seat_coach_id", columnList = "coach_id"),
				// Composite index to speed up available seat lookups: WHERE coach_id = ? AND
				// is_occupied = false
				@Index(name = "idx_seat_coach_occupancy", columnList = "coach_id, is_occupied") })
public class Seat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "seat_number", nullable = false)
	private int seatNumber; // 1 to 72

	@Enumerated(EnumType.STRING)
	@Column(name = "berth_type", nullable = false, length = 20)
	private BerthType berthType; // LOWER, MIDDLE, UPPER, SIDE_LOWER, SIDE_UPPER

	@Column(name = "is_occupied", nullable = false)
	private boolean isOccupied = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coach_id", nullable = false)
	@JsonIgnore
	private Coach coach;

	// 1. Default Constructor (Required by JPA Reflection)
	public Seat() {
	}

	// 2. Business Constructor
	public Seat(int seatNumber, BerthType berthType, Coach coach) {
		this.seatNumber = seatNumber;
		this.berthType = berthType;
		this.coach = coach;
		this.isOccupied = false;
	}

	// 3. Domain Helper: Formats physical label like "S1-45 (UPPER)"
	public String getSeatLabel() {
		String coachCode = (coach != null) ? coach.getCoachCode() : "UNKNOWN";
		return String.format("%s-%d (%s)", coachCode, seatNumber, berthType);
	}

	// 4. Domain Helper: Calculates Indian Railways 8-Berth Compartment Bay (1 to 9)
	// Seats 1-8 = Bay 1, 9-16 = Bay 2 ... 65-72 = Bay 9
	public int getBayNumber() {
		if (seatNumber < 1) {
			throw new IllegalStateException("Invalid seat number for bay calculation: " + seatNumber);
		}
		return ((seatNumber - 1) / 8) + 1;
	}

	// 5. Domain Helper: Directly checks if this seat is suitable for senior
	// citizens
	public boolean isLowerBerth() {
		return this.berthType != null && this.berthType.isLowerTier();
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(int seatNumber) {
		this.seatNumber = seatNumber;
	}

	public BerthType getBerthType() {
		return berthType;
	}

	public void setBerthType(BerthType berthType) {
		this.berthType = berthType;
	}

	public boolean isOccupied() {
		return isOccupied;
	}

	public void setOccupied(boolean occupied) {
		isOccupied = occupied;
	}

	public Coach getCoach() {
		return coach;
	}

	public void setCoach(Coach coach) {
		this.coach = coach;
	}

	// 6. Safe equals & hashCode using business key (seatNumber + coachCode)
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Seat seat))
			return false;
		return seatNumber == seat.seatNumber && Objects.equals(coach != null ? coach.getCoachCode() : null,
				seat.coach != null ? seat.coach.getCoachCode() : null);
	}

	@Override
	public int hashCode() {
		return Objects.hash(seatNumber, coach != null ? coach.getCoachCode() : null);
	}

	// 7. Safe toString (Excludes Lazy Coach to prevent StackOverflowError)
	@Override
	public String toString() {
		return "Seat{" + "id=" + id + ", seatNumber=" + seatNumber + ", berthType=" + berthType + ", isOccupied="
				+ isOccupied + '}';
	}
}
