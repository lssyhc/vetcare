const ProfileDashboard = {
  init: function () {
    this.initTabNavigation();
    this.initDeleteConfirmation();
  },

  initTabNavigation: function () {
    window.showTab = this.showTab.bind(this);
  },

  initDeleteConfirmation: function () {
    const deleteBtn = document.getElementById("deleteAccountBtn");
    const confirmDialog = document.getElementById("confirmationDialog");
    const cancelBtn = document.getElementById("cancelDelete");

    if (!deleteBtn || !confirmDialog || !cancelBtn) return;

    deleteBtn.addEventListener("click", function () {
      confirmDialog.classList.remove("hidden");
    });

    cancelBtn.addEventListener("click", function () {
      confirmDialog.classList.add("hidden");
    });

    confirmDialog.addEventListener("click", function (event) {
      if (event.target === confirmDialog) {
        confirmDialog.classList.add("hidden");
      }
    });

    document.addEventListener("keydown", function (event) {
      if (
        event.key === "Escape" &&
        !confirmDialog.classList.contains("hidden")
      ) {
        confirmDialog.classList.add("hidden");
      }
    });
  },

  showTab: function (tabId) {
    document.querySelectorAll(".profile-tab-content").forEach((tab) => {
      tab.classList.add("hidden");
    });

    const selectedTab = document.getElementById(tabId);
    if (selectedTab) {
      selectedTab.classList.remove("hidden");
    }

    document.querySelectorAll(".profile-tab-nav a").forEach((tab) => {
      tab.classList.remove("active");
    });

    if (event && event.currentTarget) {
      event.currentTarget.classList.add("active");
    }
  },
};

document.addEventListener("DOMContentLoaded", function () {
  ProfileDashboard.init();
});
