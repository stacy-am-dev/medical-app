package git.stacyamdev.medicalapp.model.database.dao;

import git.stacyamdev.medicalapp.model.entity.Doctor;
import git.stacyamdev.medicalapp.model.entity.Patient;
import git.stacyamdev.medicalapp.model.entity.Prescription;
import git.stacyamdev.medicalapp.model.exception.MedicalException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDao implements DaoInterface<Prescription> {

    private Connection connection = null;

    public PrescriptionDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Prescription persist(Prescription entity) throws MedicalException {
        Prescription prescription = new Prescription();
        String sql = "INSERT INTO TABLE_PRESCRIPTION(" +
                "DESCRIPTION, " +
                "PATIENTID, " +
                "DOCTORID, " +
                "DATACREATION, " +
                "VALIDITY, " +
                "PRIORITY) " +
                "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, entity.getDescription());
            Long patientId = null;
            if (entity.getPatient() != null)
                patientId = entity.getPatient().getId();
            preparedStatement.setLong(2, patientId);
            Long doctorId = null;
            if (entity.getDoctor() != null)
                doctorId = entity.getDoctor().getId();
            preparedStatement.setLong(3, doctorId);
            Date dataCreation = null;
            if (entity.getDataCreation() != null)
                dataCreation = new Date(entity.getDataCreation().getTime());
            preparedStatement.setDate(4, dataCreation);
            Date validity = null;
            if (entity.getValidity() != null)
                validity = new Date(entity.getValidity().getTime());
            preparedStatement.setDate(5, validity);
            preparedStatement.setString(6, entity.getPriority());
            if (preparedStatement.executeUpdate() != 1) {
                throw new MedicalException("Crating new Prescription failed");
            } else {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    resultSet.next();
                    prescription.setId(resultSet.getLong(1));
                    prescription.setDescription(entity.getDescription());
                    prescription.setPatient(entity.getPatient());
                    prescription.setDoctor(entity.getDoctor());
                    prescription.setDataCreation(entity.getDataCreation());
                    prescription.setValidity(entity.getValidity());
                    prescription.setPriority(entity.getPriority());
                }
            }
        } catch (Exception e) {
            throw new MedicalException(e);
        }
        return prescription;
    }

    @Override
    public Prescription getByKey(Long key) throws MedicalException {
        Prescription prescription = null;
        String sql = "SELECT " +
                "id, " +
                "name, " +
                "surname, " +
                "patronymic, " +
                "specialization " +
                "FROM TABLE_DOCTOR WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                prescription = new Prescription();
                Patient patient = DaoFactory.getDaoFactory().getPatientDao().getByKey(resultSet.getLong("patientId"));
                Doctor doctor = DaoFactory.getDaoFactory().getDoctorDao().getByKey(resultSet.getLong("doctorId"));
                prescription.setId(key);
                prescription.setDescription(resultSet.getString("description"));
                prescription.setPatient(patient);
                prescription.setDoctor(doctor);
                prescription.setDataCreation(resultSet.getDate("dataCreation"));
                prescription.setValidity(resultSet.getDate("validity"));
                prescription.setPriority(resultSet.getString("priority"));
            }
        } catch (Exception e) {
            throw new MedicalException(e);
        }
        return prescription;
    }

    @Override
    public void update(Prescription entity) throws MedicalException {
        String sql = "UPDATE TABLE_PRESCRIPTION SET " +
                "description = ?, " +
                "patientId = ?, " +
                "doctorId = ?, " +
                "dataCreation = ?, " +
                "validity = ?, " +
                "priority = ? " +
                "WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getDescription());
            Long patientId = null;
            if (entity.getPatient() != null)
                patientId = entity.getPatient().getId();
            preparedStatement.setLong(2, patientId);
            Long doctorId = null;
            if (entity.getDoctor() != null)
                doctorId = entity.getDoctor().getId();
            preparedStatement.setLong(3, doctorId);
            Date dataCreation = null;
            if (entity.getDataCreation() != null)
                dataCreation = new Date(entity.getDataCreation().getTime());
            preparedStatement.setDate(4, dataCreation);
            Date validity = null;
            if (entity.getValidity() != null)
                validity = new Date(entity.getValidity().getTime());
            preparedStatement.setDate(5, validity);
            preparedStatement.setString(6, entity.getPriority());
            preparedStatement.setLong(7, entity.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new MedicalException(e);
        }
    }

    @Override
    public void delete(Prescription entity) throws MedicalException {
        String sql = "DELETE FROM TABLE_PRESCRIPTION WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, entity.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new MedicalException(e);
        }
    }

    @Override
    public List<Prescription> getAll() throws MedicalException {
        List<Prescription> list = new ArrayList<>();
        String sql = "SELECT " +
                "id, " +
                "description, " +
                "patientId, " +
                "doctorId, " +
                "dataCreation, " +
                "validity, " +
                "priority " +
                "FROM TABLE_PRESCRIPTION";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Prescription prescription = new Prescription();
                Patient patient = DaoFactory.getDaoFactory().getPatientDao().getByKey(resultSet.getLong("patientId"));
                Doctor doctor = DaoFactory.getDaoFactory().getDoctorDao().getByKey(resultSet.getLong("doctorId"));
                prescription.setId(resultSet.getLong("id"));
                prescription.setDescription(resultSet.getString("description"));
                prescription.setPatient(patient);
                prescription.setDoctor(doctor);
                prescription.setDataCreation(resultSet.getDate("dataCreation"));
                prescription.setValidity(resultSet.getDate("validity"));
                prescription.setPriority(resultSet.getString("priority"));
                list.add(prescription);
            }
        } catch (Exception e) {
            throw new MedicalException(e);
        }
        return list;
    }
}
