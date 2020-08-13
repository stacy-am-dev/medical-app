package git.stacyamdev.medicalapp;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import git.stacyamdev.medicalapp.model.exception.MedicalException;
import git.stacyamdev.medicalapp.view.doctor.DoctorView;
import git.stacyamdev.medicalapp.view.patient.PatientView;
import git.stacyamdev.medicalapp.view.prescription.PrescriptionView;

@Theme(ValoTheme.THEME_NAME)
public class MainUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button buttonPatient = new Button(
                "Пациенты",
                clickEvent -> getNavigator().navigateTo(PatientView.NAME)
        );
        Button buttonDoctor = new Button(
                "Врачи",
                clickEvent -> getNavigator().navigateTo(DoctorView.NAME)
        );
        Button buttonPrescription = new Button(
                "Рецепты",
                clickEvent -> getNavigator().navigateTo(PrescriptionView.NAME)
        );
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponents(
                buttonPatient,
                buttonDoctor,
                buttonPrescription
        );

        VerticalLayout viewsLayout = new VerticalLayout();
        viewsLayout.setSizeFull();
        viewsLayout.setSpacing(true);

        layout.addComponents(horizontalLayout, viewsLayout);
        layout.setComponentAlignment(horizontalLayout, Alignment.TOP_CENTER);
        layout.setExpandRatio(viewsLayout, 1f);

        ViewDisplay viewDisplay =
                new Navigator.ComponentContainerViewDisplay(viewsLayout);
        Navigator navigator = new Navigator(this, viewDisplay);
        try {
            navigator.addView(DoctorView.NAME, new DoctorView());
            navigator.addView(PatientView.NAME, new PatientView());
            navigator.addView(PrescriptionView.NAME, new PrescriptionView());
        } catch (MedicalException e) {
            e.printStackTrace();
        }

        setContent(layout);
    }
}



