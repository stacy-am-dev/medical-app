package git.stacyamdev.medicalapp.view.patient;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import git.stacyamdev.medicalapp.model.database.dao.DaoFactory;
import git.stacyamdev.medicalapp.model.exception.MedicalException;
import git.stacyamdev.medicalapp.model.entity.Patient;

import java.util.List;

public class PatientView extends VerticalLayout implements View {

    public static final String NAME = "";
    private Grid<Patient> patientGrid;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;

    public PatientView() throws MedicalException {
        initPatientView();
        initButtonClickListener();
    }

    public void initPatientView() throws MedicalException {
        patientGrid = new Grid<>();
        patientGrid.setItems(DaoFactory.getDaoFactory().getPatientDao().getAll());
        patientGrid.addColumn(Patient::getName).setCaption("Имя");
        patientGrid.addColumn(Patient::getSurname).setCaption("Фамилия");
        patientGrid.addColumn(Patient::getPatronymic).setCaption("Отчество");
        patientGrid.addColumn(Patient::getPhoneNumber).setCaption("Номер телефона");
        patientGrid.setSizeFull();

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        addButton = new Button("Добавить");
        updateButton = new Button("Радактировать");
        updateButton.setEnabled(false);
        deleteButton = new Button("Удалить");
        deleteButton.setEnabled(false);
        horizontalLayout.addComponents(addButton, updateButton, deleteButton);

        setMargin(false);
        setSizeFull();
        addComponents(patientGrid, horizontalLayout);
    }

    public void initButtonClickListener() {
        addButton.addClickListener(clickEvent ->
                getUI().addWindow(new PatientWindow(patientGrid, false))
        );
        patientGrid.addSelectionListener(selectionEvent -> {
            if (!patientGrid.asSingleSelect().isEmpty()) {
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } else {
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        });

        deleteButton.addClickListener(clickEvent -> {
            try {
                DaoFactory.getDaoFactory().getPatientDao()
                        .delete(patientGrid.asSingleSelect().getValue());
                updatePatientGrid();
            } catch (MedicalException e) {
                if (e.getCause().getClass().equals(java.sql.SQLIntegrityConstraintViolationException.class)) {
                    Notification notification = new Notification(
                            "Удаление пациента не возможно. " +
                                    "Существует рецепт для данного пациента.");
                    notification.setDelayMsec(3000);
                    notification.show(Page.getCurrent());
                } else
                    e.printStackTrace();
            }
        });

        updateButton.addClickListener(clickEvent -> getUI().addWindow(new PatientWindow(patientGrid, true)));
    }

    private void updatePatientGrid(){
        try {
            List<Patient> patients = DaoFactory.getDaoFactory().getPatientDao()
                    .getAll();
            patientGrid.setItems(patients);
        }catch (MedicalException e){
            e.printStackTrace();
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        updatePatientGrid();
    }
}
