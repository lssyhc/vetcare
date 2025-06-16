class AppointmentBooking {
  constructor() {
    this.veterinarianSelect = document.getElementById("veterinarianId");
    this.dateInput = document.getElementById("appointmentDate");
    this.timeSelect = document.getElementById("appointmentTime");
    this.durationSelect = document.getElementById("durationMinutes");

    this.init();
  }

  init() {
    this.bindEvents();
    this.setupDatePicker();
  }

  bindEvents() {
    this.veterinarianSelect.addEventListener("change", () =>
      this.loadAvailableTimeSlots()
    );
    this.dateInput.addEventListener("change", () =>
      this.loadAvailableTimeSlots()
    );
    this.timeSelect.addEventListener("change", () =>
      this.loadAvailableDurations()
    );
  }

  setupDatePicker() {
    this.dateInput.addEventListener("click", function () {
      if (typeof this.showPicker === "function") {
        this.showPicker();
      }
    });
  }

  loadAvailableTimeSlots() {
    const veterinarianId = this.veterinarianSelect.value;
    const appointmentDate = this.dateInput.value;

    if (!veterinarianId || !appointmentDate) {
      this.resetTimeSelect();
      this.resetDurationSelect();
      return;
    }

    this.fetchTimeSlots(veterinarianId, appointmentDate);
  }

  async fetchTimeSlots(veterinarianId, appointmentDate) {
    try {
      const response = await fetch(
        `/schedule/api/schedule/${veterinarianId}/${appointmentDate}`
      );
      const data = await response.json();

      if (
        data.success &&
        data.availableSlots &&
        data.availableSlots.length > 0
      ) {
        this.populateTimeSlots(data.availableSlots);
      } else {
        this.setTimeSelectError("Tidak ada waktu tersedia");
      }
    } catch (error) {
      console.error("Error fetching time slots:", error);
      this.setTimeSelectError("Kesalahan memuat waktu");
    }
  }

  populateTimeSlots(slots) {
    this.timeSelect.innerHTML = '<option value="">-- Pilih waktu --</option>';

    slots.forEach((slot) => {
      const option = document.createElement("option");
      option.value = slot;
      option.textContent = slot;
      this.timeSelect.appendChild(option);
    });

    this.timeSelect.disabled = false;
  }

  resetTimeSelect() {
    this.timeSelect.innerHTML =
      '<option value="">-- Pilih tanggal dan veterinarian terlebih dahulu --</option>';
    this.timeSelect.disabled = true;
  }

  setTimeSelectError(message) {
    this.timeSelect.innerHTML = `<option value="">${message}</option>`;
    this.timeSelect.disabled = true;
  }

  loadAvailableDurations() {
    const veterinarianId = this.veterinarianSelect.value;
    const appointmentDate = this.dateInput.value;
    const appointmentTime = this.timeSelect.value;

    if (!veterinarianId || !appointmentDate || !appointmentTime) {
      this.resetDurationSelect();
      return;
    }

    this.fetchDurations(veterinarianId, appointmentDate, appointmentTime);
  }

  async fetchDurations(veterinarianId, appointmentDate, appointmentTime) {
    try {
      const url = `/appointments/api/available-durations?veterinarianId=${veterinarianId}&appointmentDate=${appointmentDate}&appointmentTime=${appointmentTime}`;
      const response = await fetch(url);
      const data = await response.json();

      if (
        data.success &&
        data.availableDurations &&
        data.availableDurations.length > 0
      ) {
        this.populateDurations(data.availableDurations);
      } else {
        this.setDurationSelectError("Tidak ada durasi tersedia");
      }
    } catch (error) {
      console.error("Kesalahan memuat durasi:", error);
      this.setDurationSelectError("Kesalahan memuat durasi");
    }
  }

  populateDurations(durations) {
    this.durationSelect.innerHTML =
      '<option value="">-- Pilih durasi --</option>';

    durations.forEach((duration) => {
      const option = document.createElement("option");
      option.value = duration;
      option.textContent = `${duration} menit`;
      this.durationSelect.appendChild(option);
    });

    this.durationSelect.disabled = false;
  }

  resetDurationSelect() {
    this.durationSelect.innerHTML =
      '<option value="">-- Pilih waktu terlebih dahulu --</option>';
    this.durationSelect.disabled = true;
  }

  setDurationSelectError(message) {
    this.durationSelect.innerHTML = `<option value="">${message}</option>`;
    this.durationSelect.disabled = true;
  }
}

function formatDateIndonesian(dateString) {
  const options = { day: "numeric", month: "long", year: "numeric" };
  const date = new Date(dateString);
  return date.toLocaleDateString("id-ID", options);
}

document.addEventListener("DOMContentLoaded", function () {
  new AppointmentBooking();
});

if (typeof module !== "undefined" && module.exports) {
  module.exports = { AppointmentBooking, formatDateIndonesian };
}
