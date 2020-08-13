package git.stacyamdev.medicalapp.view.doctor;

import com.vaadin.data.Binder;
import com.vaadin.ui.*;
import git.stacyamdev.medicalapp.model.database.dao.DaoFactory;
import git.stacyamdev.medicalapp.model.database.dao.DoctorDao;
import git.stacyamdev.medicalapp.model.exception.MedicalException;
import git.stacyamdev.medicalapp.model.entity.Doctor;

import java.util.List;

public class DoctorWindow extends Window {

    private Grid<Doctor> doctorGrid;
    private boolean selection;
    private TextField nameText;
    private TextField surnameText;
    private TextField patronymicText;
    private TextField specializationText;
    private Button addButton;
    private Button cancelButton;
    private Doctor doctorEdit;

    Binder<Doctor> binder = new Binder<>(Doctor.class);


    public DoctorWindow(Grid<Doctor> doctorGrid, boolean selection) {
        this.doctorGrid = doctorGrid;
        this.selection = selection;
        initDoctorWindow();
        initButtonClickListener();
    }

    public void initDoctorWindow() {
        VerticalLayout verticalLayout = new VerticalLayout();

        nameText = new TextField("Имя");
        nameText.setMaxLength(30);
        nameText.setWidth("100%");
        nameText.setRequiredIndicatorVisible(true);
        binder.forField(nameText).withValidator(name ->
                        name != null && !name.isEmpty(),
                "Введите имя."
        ).bind(Doctor::getName, Doctor::setName);

        surnameText = new TextField("Фамилия");
        surnameText.setMaxLength(30);
        surnameText.setWidth("100%");
        surnameText.setRequiredIndicatorVisible(true);
        binder.forField(surnameText).withValidator(surname ->
                        surname != null && !surname.isEmpty(),
                "Введите фамилию."
        ).bind(Doctor::getSurname, Doctor::setSurname);

        patronymicText = new TextField("Отчество");
        patronymicText.setMaxLength(30);
        patronymicText.setWidth("100%");
        patronymicText.setRequiredIndicatorVisible(true);
        binder.forField(patronymicText).withValidator(patronymic ->
                        patronymic != null && !patronymic.isEmpty(),
                "Введите отчество."
        ).bind(Doctor::getPatronymic, Doctor::setPatronymic);

        specializationText = new TextField("Специализация");
        specializationText.setMaxLength(11);
        specializationText.setWidth("100%");
        specializationText.setRequiredIndicatorVisible(true);
        binder.forField(specializationText).withValidator(specialization ->
                        specialization != null && !specialization.isEmpty(),
                "Введите специализацию."
        ).bind(Doctor::getSpecialization, Doctor::setSpecialization);

        HorizontalLayout horizontalButtonLayout = new HorizontalLayout();
        addButton = new Button("ОК");
        cancelButton = new Button("Отмена");
        horizontalButtonLayout.addComponents(addButton, cancelButton);

        verticalLayout.addComponents(
                nameText,
                surnameText,
                patronymicText,
                specializationText,
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
            setCaption("Редактирование врача");
            if (!doctorGrid.asSingleSelect().isEmpty()) {
                doctorEdit = doctorGrid.asSingleSelect().getValue();
                binder.setBean(doctorEdit);
            }
        } else {
            setCaption("Добавление нового врача");
            nameText.focus();
        }

        addButton.addClickListener(clickEvent -> {
            if (binder.validate().isOk()) {
                try {
                    Doctor doctor = new Doctor();
                    doctor.setName(nameText.getValue());
                    doctor.setSurname(surnameText.getValue());
                    doctor.setPatronymic(patronymicText.getValue());
                    doctor.setSpecialization(specializationText.getValue());
                    DoctorDao doctorDao = DaoFactory.getDaoFactory().getDoctorDao();
                    if (selection) {
                        doctor.setId(doctorEdit.getId());
                        doctorDao.update(doctor);
                    } else
                        doctorDao.persist(doctor);
                    List<Doctor> doctorList = DaoFactory.getDaoFactory().getDoctorDao()
                            .getAll();
                    doctorGrid.setItems(doctorList);
                } catch (MedicalException e) {
                    e.printStackTrace();
                }
                close();
            }
        });

        cancelButton.addClickListener(clickEvent -> close());
    }
}
