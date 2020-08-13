package git.stacyamdev.medicalapp.view.patient;

import com.vaadin.data.Binder;
import com.vaadin.ui.*;
import git.stacyamdev.medicalapp.model.database.dao.DaoFactory;
import git.stacyamdev.medicalapp.model.exception.MedicalException;
import git.stacyamdev.medicalapp.model.database.dao.PatientDao;
import git.stacyamdev.medicalapp.model.entity.Patient;

import java.util.List;

public class PatientWindow extends Window {

    private final Grid<Patient> patientGrid;
    private final boolean selection;
    private TextField nameText;
    private TextField surnameText;
    private TextField patronymicText;
    private TextField phoneNumberText;
    private Button addButton;
    private Button cancelButton;

    private Patient patientEdit;

    Binder<Patient> binder = new Binder<>(Patient.class);


    public PatientWindow(Grid<Patient> patientGrid, boolean selection) {
        this.patientGrid = patientGrid;
        this.selection = selection;
        initPatientWindow();
        initButtonClickListener();
    }

    public void initPatientWindow() {
        VerticalLayout verticalLayout = new VerticalLayout();

        nameText = new TextField("Имя");
        nameText.setMaxLength(30);
        nameText.setWidth("100%");
        nameText.setRequiredIndicatorVisible(true);
        binder.forField(nameText).withValidator(name ->
                        name != null && !name.isEmpty(),
                "Введите имя."
        ).bind(Patient::getName, Patient::setName);

        surnameText = new TextField("Фамилия");
        surnameText.setMaxLength(30);
        surnameText.setWidth("100%");
        surnameText.setRequiredIndicatorVisible(true);
        binder.forField(surnameText).withValidator(surname ->
                        surname != null && !surname.isEmpty(),
                "Введите фамилию."
        ).bind(Patient::getSurname, Patient::setSurname);

        patronymicText = new TextField("Отчество");
        patronymicText.setMaxLength(30);
        patronymicText.setWidth("100%");
        patronymicText.setRequiredIndicatorVisible(true);
        binder.forField(patronymicText).withValidator(patronymic ->
                        patronymic != null && !patronymic.isEmpty(),
                "Введите отчество."
        ).bind(Patient::getPatronymic, Patient::setPatronymic);

        phoneNumberText = new TextField("Номер телефона");
        phoneNumberText.setMaxLength(11);
        phoneNumberText.setWidth("100%");
        phoneNumberText.setRequiredIndicatorVisible(true);
        binder.forField(phoneNumberText).withValidator(phone ->
                        phone.length() == 11,
                "Введите корректный номер (должен содержать 11 цифр)"
        ).bind(Patient::getPhoneNumber, Patient::setPhoneNumber);

        HorizontalLayout horizontalButtonLayout = new HorizontalLayout();
        addButton = new Button("ОК");
        cancelButton = new Button("Отмена");
        horizontalButtonLayout.addComponents(addButton, cancelButton);

        verticalLayout.addComponents(
                nameText,
                surnameText,
                patronymicText,
                phoneNumberText,
                horizontalButtonLayout
        );
        verticalLayout.setComponentAlignment(
                horizontalButtonLayout,
                Alignment.BOTTOM_CENTER
        );
        setWidth("400px");
        setHeight("400px");
        setModal(true);
        setResizable(false);
        center();
        setContent(verticalLayout);
    }

    public void initButtonClickListener() {
        if (selection) {
            setCaption("Редактирование пациента");
            if (!patientGrid.asSingleSelect().isEmpty()) {
                patientEdit = patientGrid.asSingleSelect().getValue();
                binder.setBean(patientEdit);
            }
        } else {
            setCaption("Добавление нового пациента");
            nameText.focus();
        }

        addButton.addClickListener(clickEvent -> {
            if (binder.validate().isOk()) {
                try {
                    Patient patient = new Patient();
                    patient.setName(nameText.getValue());
                    patient.setSurname(surnameText.getValue());
                    patient.setPatronymic(patronymicText.getValue());
                    patient.setPhoneNumber(phoneNumberText.getValue());
                    PatientDao patientDao = DaoFactory.getDaoFactory().getPatientDao();
                    if (selection) {
                        patient.setId(patientEdit.getId());
                        patientDao.update(patient);
                    } else
                        patientDao.persist(patient);
                    List<Patient> patientList = DaoFactory.getDaoFactory().getPatientDao()
                            .getAll();
                    patientGrid.setItems(patientList);
                } catch (MedicalException e) {
                    e.printStackTrace();
                }
                close();
            }
        });
        cancelButton.addClickListener(clickEvent -> close());
    }
}
