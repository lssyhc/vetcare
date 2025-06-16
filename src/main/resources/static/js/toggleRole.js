class ToggleRole {
  constructor() {
    this.rolePemilik = null;
    this.roleVet = null;
    this.spesialisasiContainer = null;
    this.spesialisasiInput = null;

    this.init();
  }

  init() {
    document.addEventListener("DOMContentLoaded", () => {
      this.initializeElements();
      this.bindEvents();
      this.setInitialState();
    });
  }

  initializeElements() {
    this.rolePemilik = document.getElementById("role-pemilik");
    this.roleVet = document.getElementById("role-vet");
    this.spesialisasiContainer = document.getElementById(
      "spesialisasi-container"
    );
    this.spesialisasiInput = document.getElementById("spesialisasi");

    if (
      !this.rolePemilik ||
      !this.roleVet ||
      !this.spesialisasiContainer ||
      !this.spesialisasiInput
    ) {
      console.warn("ToggleRole: One or more required elements not found");
      return false;
    }

    return true;
  }

  bindEvents() {
    if (!this.initializeElements()) return;

    this.rolePemilik.addEventListener("change", () =>
      this.toggleSpesialisasi()
    );
    this.roleVet.addEventListener("change", () => this.toggleSpesialisasi());
  }

  toggleSpesialisasi() {
    if (!this.roleVet || !this.spesialisasiContainer || !this.spesialisasiInput)
      return;

    if (this.roleVet.checked) {
      this.showSpesialisasi();
    } else {
      this.hideSpesialisasi();
    }
  }

  showSpesialisasi() {
    this.spesialisasiContainer.style.display = "block";
    this.spesialisasiInput.setAttribute("required", "required");
  }

  hideSpesialisasi() {
    this.spesialisasiContainer.style.display = "none";
    this.spesialisasiInput.removeAttribute("required");
    this.spesialisasiInput.value = "";
  }

  setInitialState() {
    this.toggleSpesialisasi();
  }
}

new ToggleRole();
