package git.stacyamdev.medicalapp.model.database.dao;

import git.stacyamdev.medicalapp.model.database.DBHelper;
import git.stacyamdev.medicalapp.model.exception.MedicalException;

public class DaoFactory {

    private static DaoFactory daoFactory = null;

    private DaoFactory() {
    }

    public static synchronized DaoFactory getDaoFactory() {
        if (daoFactory == null) {
            daoFactory = new DaoFactory();
        }
        return daoFactory;
    }

    public PatientDao getPatientDao() throws MedicalException {
        try {
            return new PatientDao(DBHelper.getConnection());
        } catch (Exception e) {
            throw new MedicalException(e);
        }
    }

    public DoctorDao getDoctorDao() throws MedicalException {
        try {
            return new DoctorDao(DBHelper.getConnection());
        } catch (Exception e) {
            throw new MedicalException(e);
        }
    }

    public PrescriptionDao getPrescriptionDao() throws MedicalException {
        try {
            return new PrescriptionDao(DBHelper.getConnection());
        } catch (Exception e) {
            throw new MedicalException(e);
        }
    }

    public void closeDaoConnection() throws MedicalException {
        try {
            DBHelper.closeConnection();
        } catch (Exception e) {
            throw new MedicalException(e);
        }
    }

}
