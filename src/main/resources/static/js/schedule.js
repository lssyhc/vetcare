const ScheduleSetup = {
  init: function () {
    this.initSaveAll();
    this.initTimeInputs();
  },

  initSaveAll: function () {
    const saveAllForm = document.getElementById("save-all-form");
    if (!saveAllForm) return;

    saveAllForm.addEventListener("submit", function () {
      for (let day = 1; day <= 7; day++) {
        const dayForm = document
          .querySelector(`form input[name="dayOfWeek"][value="${day}"]`)
          .closest("form");

        if (!dayForm) continue;

        const isActiveCheckbox = dayForm.querySelector(
          'input[name="isActive"]'
        );
        const startTimeInput = dayForm.querySelector('input[name="startTime"]');
        const endTimeInput = dayForm.querySelector('input[name="endTime"]');

        const hiddenIsActive = saveAllForm.querySelector(
          `input[name="day${day}_isActive"]`
        );
        const hiddenStartTime = saveAllForm.querySelector(
          `input[name="day${day}_startTime"]`
        );
        const hiddenEndTime = saveAllForm.querySelector(
          `input[name="day${day}_endTime"]`
        );

        if (hiddenIsActive && isActiveCheckbox) {
          hiddenIsActive.value = isActiveCheckbox.checked;
        }
        if (hiddenStartTime && startTimeInput) {
          hiddenStartTime.value = startTimeInput.value || "";
        }
        if (hiddenEndTime && endTimeInput) {
          hiddenEndTime.value = endTimeInput.value || "";
        }
      }
    });
  },

  initTimeInputs: function () {
    const timeInputs = document.querySelectorAll(
      '.schedule-time-input[type="time"]'
    );

    timeInputs.forEach(function (input) {
      input.type = "text";
      input.placeholder = "JJ:MM";
      input.maxLength = 5;

      input.addEventListener("input", ScheduleSetup.handleTimeInput);
      input.addEventListener("blur", ScheduleSetup.handleTimeBlur);
    });
  },

  handleTimeInput: function (event) {
    const input = event.target;
    input.value = ScheduleSetup.formatTime(input.value);
  },

  handleTimeBlur: function (event) {
    const input = event.target;
    if (input.value.length === 5 && !ScheduleSetup.isValidTime(input.value)) {
      input.value = "08:00";
    }
  },

  formatTime: function (value) {
    value = value.replace(/[^\d:]/g, "");

    if (value.length === 2 && !value.includes(":")) {
      value += ":";
    }

    return value.substring(0, 5);
  },

  isValidTime: function (value) {
    return /^([0-1]?\d|2[0-3]):([0-5]?\d)$/.test(value);
  },
};

document.addEventListener("DOMContentLoaded", function () {
  ScheduleSetup.init();
});
