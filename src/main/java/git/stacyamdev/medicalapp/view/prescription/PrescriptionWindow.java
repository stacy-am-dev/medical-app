package git.stacyamdev.medicalapp.view.prescription;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.ui.*;
import git.stacyamdev.medicalapp.model.database.dao.DaoFactory;
import git.stacyamdev.medicalapp.model.exception.MedicalException;
import git.stacyamdev.medicalapp.model.database.dao.PrescriptionDao;
import git.stacyamdev.medicalapp.model.entity.Doctor;
import git.stacyamdev.medicalapp.model.entity.Patient;
import git.stacyamdev.medicalapp.model.entity.Prescription;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PrescriptionWindow extends Window {

    private final Grid<Prescription> prescriptionGrid;
    private final boolean selection;
    private TextField descriptionText;
    private ComboBox<Patient> patientComboBox;
    private ComboBox<Doctor> doctorComboBox;
    private DateField dataCreationField;
    private DateField validityField;
    private ComboBox<String> priorityComboBox;
    private Button addButton;
    private Button cancelButton;
    private Prescription prescriptionEdit;

    Binder<Prescription> binder = new Binder<>(Prescription.class);


    public PrescriptionWindow(Grid<Prescription> patientGrid, boolean selection) {
        this.prescriptionGrid = patientGrid;
        this.selection = selection;
        initPrescriptionWindow();
        initButtonClickListener();
    }

    public void initPrescriptionWindow() {
        VerticalLayout verticalLayout = new VerticalLayout();

        descriptionText = new TextField("Описание");
        descriptionText.setMaxLength(1000);
        descriptionText.setWidth("100%");
        descriptionText.setRequiredIndicatorVisible(true);
        binder.forField(descriptionText).withValidator(description ->
                        description != null && !description.isEmpty(),
                "Введите описание."
        ).bind(Prescription::getDescription, Prescription::setDescription);

        patientComboBox = new ComboBox<>("Пациент");
        try {
            List<Patient> patients = DaoFactory.getDaoFactory().getPatientDao()
                    .getAll();
            patientComboBox.setItems(patients);
            patientComboBox.setItemCaptionGenerator(patient ->
                    patient.getName() + " " + patient.getSurname()
            );
        } catch (MedicalException e) {
            e.printStackTrace();
        }
        patientComboBox.setWidth("100%");
        patientComboBox.setTextInputAllowed(false);
        binder.forField(patientComboBox)
                .withValidator(Objects::nonNull, "Выберите пациента.")
                .bind(Prescription::getPatient, Prescription::setPatient);

        doctorComboBox = new ComboBox<>("Врач");
        try {
            List<Doctor> doctors = DaoFactory.getDaoFactory().getDoctorDao()
                    .getAll();
            doctorComboBox.setItems(doctors);
            doctorComboBox.setItemCaptionGenerator(doctor ->
                    doctor.getName() + " " + doctor.getSurname()
            );
        } catch (MedicalException e) {
            e.printStackTrace();
        }
        doctorComboBox.setWidth("100%");
        doctorComboBox.setTextInputAllowed(false);
        binder.forField(doctorComboBox)
                .withValidator(Objects::nonNull, "Выберите врача.")
                .bind(Prescription::getDoctor, Prescription::setDoctor);

        dataCreationField = new DateField("Дата создания");
        dataCreationField.setDateFormat("yyyy-MM-dd");
        dataCreationField.setPlaceholder("гггг-мм-дд");
        dataCreationField.setWidth("100%");
        dataCreationField.setTextFieldEnabled(false);
        binder.forField(dataCreationField)
                .withConverter(new LocalDateToDateConverter(ZoneId.systemDefault()))
                .withValidator(Objects::nonNull, "Введите дату создания рецепта.")
                .bind(Prescription::getDataCreation, Prescription::setDataCreation);

        validityField = new DateField("Дата истечения срока");
        validityField.setDateFormat("yyyy-MM-dd");
        validityField.setPlaceholder("гггг-мм-дд");
        validityField.setWidth("100%");
        validityField.setTextFieldEnabled(false);
        binder.forField(validityField)
                .withConverter(new LocalDateToDateConverter(ZoneId.systemDefault()))
                .withValidator(Objects::nonNull, "Введите дату истечения срока рецепта.")
                .bind(Prescription::getValidity, Prescription::setValidity);

        priorityComboBox = new ComboBox<>("Приоритет");
        List<String> priorityList = new ArrayList<>();
        priorityList.add("Нормальный");
        priorityList.add("Срочный");
        priorityList.add("Немедленный");
        priorityComboBox.setItems(priorityList);
        priorityComboBox.setWidth("100%");
        priorityComboBox.setTextInputAllowed(false);
        binder.forField(priorityComboBox)
                .withValidator(Objects::nonNull, "Выберите приоритет.")
                .bind(Prescription::getPriority, Prescription::setPriority);

        HorizontalLayout horizontalButtonLayout = new HorizontalLayout();
        addButton = new Button("ОК");
        cancelButton = new Button("Отмена");
        horizontalButtonLayout.addComponents(addButton, cancelButton);

        verticalLayout.addComponents(
                descriptionText,
                patientComboBox,
                doctorComboBox,
                dataCreationField,
                validityField,
                priorityComboBox,
                horizontalButtonLayout
        );
        verticalLayout.setComponentAlignment(
                horizontalButtonLayout,
                Alignment.BOTTOM_CENTER
        );
        setWidth("400px");
        setHeight("600px");
        setModal(true);
        setResizable(false);
        center();
        setContent(verticalLayout);
    }

    public void initButtonClickListener() {
        if (selection) {
            setCaption("Редактирование рецепта");
            if (!prescriptionGrid.asSingleSelect().isEmpty()) {
                prescriptionEdit = prescriptionGrid.asSingleSelect().getValue();
                binder.setBean(prescriptionEdit);
            }
        } else {
            setCaption("Добавление нового рецепта");
            descriptionText.focus();
        }

        addButton.addClickListener(clickEvent -> {
            if (binder.validate().isOk()) {
                try {
                    Prescription prescription = new Prescription();
                    prescription.setDescription(descriptionText.getValue());
                    prescription.setPatient(patientComboBox.getValue());
                    prescription.setDoctor(doctorComboBox.getValue());
                    prescription.setDataCreation(
                            Date.from(
                                    dataCreationField.getValue().atStartOfDay(
                                            ZoneId.systemDefault()
                                    ).toInstant()
                            )
                    );
                    prescription.setValidity(
                            Date.from(
                                    validityField.getValue().atStartOfDay(
                                            ZoneId.systemDefault()
                                    ).toInstant()
                            )
                    );
                    prescription.setPriority(priorityComboBox.getValue());
                    PrescriptionDao prescriptionDao =
                            DaoFactory.getDaoFactory().getPrescriptionDao();
                    if (selection) {
                        prescription.setId(prescriptionEdit.getId());
                        prescriptionDao.update(prescription);
                    } else
                        prescriptionDao.persist(prescription);
                    List<Prescription> prescriptionList =
                            DaoFactory.getDaoFactory().getPrescriptionDao()
                                    .getAll();
                    prescriptionGrid.setItems(prescriptionList);
                } catch (MedicalException e) {
                    e.printStackTrace();
                }
                close();
            }
        });
        cancelButton.addClickListener(clickEvent -> close());
    }
}
