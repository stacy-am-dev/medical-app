package git.stacyamdev.medicalapp.view.prescription;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import git.stacyamdev.medicalapp.model.database.dao.DaoFactory;
import git.stacyamdev.medicalapp.model.exception.MedicalException;
import git.stacyamdev.medicalapp.model.entity.Prescription;

import java.util.List;

public class PrescriptionView extends VerticalLayout implements View {

    public static final String NAME = "prescriptions";
    private Grid<Prescription> prescriptionGrid;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;
    private Button applyButton;
    private TextField patientFilterText;
    private TextField priorityFilterText;
    private TextField descriptionFilterText;

    public PrescriptionView() throws MedicalException {
        initPrescriptionView();
        initButtonClickListener();
    }

    public void initPrescriptionView() throws MedicalException {
        prescriptionGrid = new Grid<>();
        prescriptionGrid.setItems(DaoFactory.getDaoFactory().getPrescriptionDao().getAll());
        prescriptionGrid.addColumn(Prescription::getDescription).setCaption("Описание");
        prescriptionGrid.addColumn(prescription ->
                prescription.getPatient().getName() + " " +
                        prescription.getPatient().getSurname() + " " +
                        prescription.getPatient().getPatronymic()
        ).setCaption("Пациент");
        prescriptionGrid.addColumn(prescription ->
                prescription.getDoctor().getName() + " " +
                        prescription.getDoctor().getSurname() + " " +
                        prescription.getDoctor().getPatronymic()
        ).setCaption("Доктор");
        prescriptionGrid.addColumn(Prescription::getDataCreation).setCaption("Дата создания");
        prescriptionGrid.addColumn(Prescription::getValidity).setCaption("Срок действия");
        prescriptionGrid.addColumn(Prescription::getPriority).setCaption("Приоритет");
        prescriptionGrid.setSizeFull();

        HorizontalLayout filterHorizontalLayout = new HorizontalLayout();
        filterHorizontalLayout.setCaption("Фильтры списка рецептов");
        patientFilterText = new TextField("Пациент");
        priorityFilterText = new TextField("Приоритет");
        descriptionFilterText = new TextField("Описание");
        applyButton = new Button("Применить");
        filterHorizontalLayout.addComponents(
                patientFilterText,
                priorityFilterText,
                descriptionFilterText,
                applyButton
        );
        filterHorizontalLayout.setComponentAlignment(
                applyButton,
                Alignment.BOTTOM_CENTER
        );

        HorizontalLayout buttonHorizontalLayout = new HorizontalLayout();
        buttonHorizontalLayout.setSpacing(true);
        addButton = new Button("Добавить");
        updateButton = new Button("Радактировать");
        updateButton.setEnabled(false);
        deleteButton = new Button("Удалить");
        deleteButton.setEnabled(false);
        buttonHorizontalLayout.addComponents(
                addButton,
                updateButton,
                deleteButton
        );

        setMargin(false);
        setSizeFull();
        addComponents(
                filterHorizontalLayout,
                prescriptionGrid,
                buttonHorizontalLayout
        );
    }

    public void initButtonClickListener() {
        addButton.addClickListener(clickEvent ->
                getUI().addWindow(new PrescriptionWindow(prescriptionGrid, false)));
        prescriptionGrid.addSelectionListener(selectionEvent -> {
            if (!prescriptionGrid.asSingleSelect().isEmpty()) {
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } else {
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        });

        deleteButton.addClickListener(clickEvent -> {
            try {
                DaoFactory.getDaoFactory().getPrescriptionDao()
                        .delete(prescriptionGrid.asSingleSelect().getValue());
                updatePrescriptionGrid();
            } catch (MedicalException e) {
                e.printStackTrace();
            }
        });

        updateButton.addClickListener(clickEvent ->
                getUI().addWindow(new PrescriptionWindow(prescriptionGrid, true)));

        applyButton.addClickListener(clickEvent -> {
            ListDataProvider<Prescription> dataProvider = (ListDataProvider<Prescription>) prescriptionGrid.getDataProvider();
            dataProvider.setFilter((item) -> {
                boolean patientFilter = (item.getPatient().getSurname() + " " +
                        item.getPatient().getName())
                        .toLowerCase()
                        .contains(patientFilterText.getValue().toLowerCase());
                boolean priorityFilter = (item.getPriority()
                        .toLowerCase()
                        .contains(priorityFilterText.getValue().toLowerCase()));
                boolean descriptionFilter = (item.getDescription()
                        .toLowerCase()
                        .contains(descriptionFilterText.getValue().toLowerCase()));
                return patientFilter && priorityFilter && descriptionFilter;
            });

        });
    }

    private void updatePrescriptionGrid() {
        try {
            List<Prescription> prescriptions = DaoFactory.getDaoFactory().getPrescriptionDao().getAll();
            prescriptionGrid.setItems(prescriptions);
        } catch (MedicalException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        updatePrescriptionGrid();
    }
}
