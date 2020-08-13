package git.stacyamdev.medicalapp.view.doctor;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Window;
import git.stacyamdev.medicalapp.model.database.dao.DaoFactory;
import git.stacyamdev.medicalapp.model.exception.MedicalException;
import git.stacyamdev.medicalapp.model.entity.Doctor;
import git.stacyamdev.medicalapp.model.entity.Prescription;

import java.util.List;

public class StatisticsWindow extends Window {

    public StatisticsWindow() {
        initStatisticsWindow();
    }

    public void initStatisticsWindow() {
        setCaption("Статистическая информации по количеству рецептов");

        Grid<Doctor> doctorGrid = new Grid<>();

        doctorGrid.addColumn(doctor ->
                doctor.getName() + " " +
                        doctor.getSurname() + " " +
                        doctor.getPatronymic()
        ).setCaption("Врач");
        doctorGrid.addColumn(doctor -> {
            int count = 0;
            try {
                List<Prescription> prescriptionList =
                        DaoFactory.getDaoFactory().getPrescriptionDao().getAll();
                for (Prescription prescription : prescriptionList) {
                    if (prescription.getDoctor().getId().equals(doctor.getId()))
                        count++;
                }
            } catch (MedicalException e) {
                e.printStackTrace();
            }
            return count;
        }).setCaption("Количество рецептов");
        try {
            doctorGrid.setItems(DaoFactory.getDaoFactory().getDoctorDao().getAll());
        } catch (MedicalException e) {
            e.printStackTrace();
        }
        doctorGrid.setSizeFull();
        setWidth("700px");
        setHeight("500px");
        setModal(true);
        setResizable(false);
        center();
        setContent(doctorGrid);
    }
}
