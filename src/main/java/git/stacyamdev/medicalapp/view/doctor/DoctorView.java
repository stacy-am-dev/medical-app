package git.stacyamdev.medicalapp.view.doctor;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import git.stacyamdev.medicalapp.model.database.dao.DaoFactory;
import git.stacyamdev.medicalapp.model.exception.MedicalException;
import git.stacyamdev.medicalapp.model.entity.Doctor;

import java.util.List;

public class DoctorView extends VerticalLayout implements View {

    public static final String NAME = "doctors";
    private Grid<Doctor> doctorGrid;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private Button showStatistics;

    public DoctorView() throws MedicalException {
        initDoctorView();
        initButtonClickListener();
    }

    public void initDoctorView() throws MedicalException {
        doctorGrid = new Grid<>();
        doctorGrid.setItems(DaoFactory.getDaoFactory().getDoctorDao().getAll());
        doctorGrid.addColumn(Doctor::getName).setCaption("Имя");
        doctorGrid.addColumn(Doctor::getSurname).setCaption("Фамилия");
        doctorGrid.addColumn(Doctor::getPatronymic).setCaption("Отчество");
        doctorGrid.addColumn(Doctor::getSpecialization).setCaption("Специализация");
        doctorGrid.setSizeFull();

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        addButton = new Button("Добавить");
        updateButton = new Button("Радактировать");
        updateButton.setEnabled(false);
        deleteButton = new Button("Удалить");
        deleteButton.setEnabled(false);
        showStatistics = new Button("Показать статистику");
        horizontalLayout.addComponents(
                addButton,
                updateButton,
                deleteButton,
                showStatistics
        );

        setMargin(false);
        setSizeFull();
        addComponents(doctorGrid, horizontalLayout);
    }

    public void initButtonClickListener() {
        addButton.addClickListener(clickEvent ->
                getUI().addWindow(new DoctorWindow(doctorGrid, false))
        );

        doctorGrid.addSelectionListener(selectionEvent -> {
            if (!doctorGrid.asSingleSelect().isEmpty()) {
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } else {
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        });

        deleteButton.addClickListener(clickEvent -> {
            try {
                DaoFactory.getDaoFactory().getDoctorDao()
                        .delete(doctorGrid.asSingleSelect().getValue());
                updateDoctorGrid();
            } catch (MedicalException e) {
                if (e.getCause().getClass().equals(java.sql.SQLIntegrityConstraintViolationException.class)) {
                    Notification notification = new Notification(
                            "Удаление врача не возможно. " +
                                    "Существует рецепт для данного врача."
                    );
                    notification.setDelayMsec(3000);
                    notification.show(Page.getCurrent());
                } else
                    e.printStackTrace();
            }
        });

        updateButton.addClickListener(clickEvent ->
                getUI().addWindow(new DoctorWindow(doctorGrid, true))
        );

        showStatistics.addClickListener(clickEvent ->
                getUI().addWindow(new StatisticsWindow())
        );
    }

    private void updateDoctorGrid(){
        try {
            List<Doctor> doctors = DaoFactory.getDaoFactory().getDoctorDao()
                    .getAll();
            doctorGrid.setItems(doctors);
        } catch (MedicalException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
            updateDoctorGrid();
    }
}
