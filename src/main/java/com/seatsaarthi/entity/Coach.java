package com.seatsaarthi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seatsaarthi.model.enums.CoachType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a physical railway coach (e.g., "S1", "S2", "B1") attached to a
 * specific train. Manages its seat capacity and individual berth topology.
 */
@Entity
@Table(name = "coaches", uniqueConstraints = {
		// Coach code (e.g. "S1") must be unique WITHIN the same train
		@UniqueConstraint(name = "uk_coach_code_per_train", columnNames = { "coach_code", "train_id" }) }, indexes = {
				@Index(name = "idx_coach_train_id", columnList = "train_id") })
public class Coach {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "coach_code", nullable = false, length = 10)
	private String coachCode; // e.g., "S1", "S2", "B1"

	@Enumerated(EnumType.STRING)
	@Column(name = "coach_type", nullable = false, length = 20)
	private CoachType coachType; // SLEEPER, AC_3_TIER, etc.

	@Column(name = "total_seats", nullable = false)
	private int totalSeats; // Typically 72 for Sleeper / 3AC

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "train_id", nullable = false)
	@JsonIgnore
	private Train train;

	@OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Seat> seats = new ArrayList<>();

	// 1. Default Constructor (Required by JPA Reflection)
	public Coach() {
	}

	// 2. Business Constructor
	public Coach(String coachCode, CoachType coachType, int totalSeats, Train train) {
		this.coachCode = coachCode;
		this.coachType = coachType;
		this.totalSeats = totalSeats;
		this.train = train;
	}

	// 3. Child Synchronization Helper (Maintains Seat -> Coach reference)
	public void addSeat(Seat seat) {
		this.seats.add(seat);
		seat.setCoach(this);
	}

	public void removeSeat(Seat seat) {
		this.seats.remove(seat);
		seat.setCoach(null);
	}

	// 4. Domain Helper Method
	public boolean isAcCoach() {
		return this.coachType != CoachType.SLEEPER;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCoachCode() {
		return coachCode;
	}

	public void setCoachCode(String coachCode) {
		this.coachCode = coachCode;
	}

	public CoachType getCoachType() {
		return coachType;
	}

	public void setCoachType(CoachType coachType) {
		this.coachType = coachType;
	}

	public int getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}

	public Train getTrain() {
		return train;
	}

	public void setTrain(Train train) {
		this.train = train;
	}

	public List<Seat> getSeats() {
		return seats;
	}

	public void setSeats(List<Seat> seats) {
		this.seats = seats;
	}

	// 5. Safe equals & hashCode using business key (coachCode + train number)
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Coach coach))
			return false;
		return Objects.equals(coachCode, coach.coachCode)
				&& Objects.equals(train != null ? train.getTrainNumber() : null,
						coach.train != null ? coach.train.getTrainNumber() : null);
	}

	@Override
	public int hashCode() {
		return Objects.hash(coachCode, train != null ? train.getTrainNumber() : null);
	}

	// 6. Safe toString (Excludes Lazy collections: seats and train to prevent
	// StackOverflowError)
	@Override
	public String toString() {
		return "Coach{" + "id=" + id + ", coachCode='" + coachCode + '\'' + ", coachType=" + coachType + ", totalSeats="
				+ totalSeats + '}';
	}
}
