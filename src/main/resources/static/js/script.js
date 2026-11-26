// Campus Mystery Finder general scripts

document.addEventListener('DOMContentLoaded', () => {
  // Login Tab Switching (Condition 8)
  const tabBtns = document.querySelectorAll('.login-tab-btn');
  const tabForms = document.querySelectorAll('.login-tab-content');

  tabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      const target = btn.getAttribute('data-target');

      tabBtns.forEach(b => b.classList.remove('active'));
      tabForms.forEach(f => f.classList.remove('active'));

      btn.classList.add('active');
      const targetForm = document.getElementById(target);
      if (targetForm) targetForm.classList.add('active');
    });
  });

  // Dynamic electronic category toggle
  const isElectronicRadios = document.querySelectorAll('input[name="isElectronic"], input[name="claimIsElectronic"]');
  const electronicCategorySection = document.getElementById('electronicDetailsSection');

  if (isElectronicRadios.length > 0 && electronicCategorySection) {
    isElectronicRadios.forEach(radio => {
      radio.addEventListener('change', () => {
        if (radio.value === 'true' && radio.checked) {
          electronicCategorySection.style.display = 'block';
        } else if (radio.value === 'false' && radio.checked) {
          electronicCategorySection.style.display = 'none';
        }
      });
    });
  }
});
