package git.stacyamdev.medicalapp.model.database.dao;

import git.stacyamdev.medicalapp.model.entity.Patient;
import git.stacyamdev.medicalapp.model.exception.MedicalException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PatientDao implements DaoInterface<Patient> {

    private Connection connection = null;

    public PatientDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Patient persist(Patient entity) throws MedicalException {
        Patient patient = new Patient();
        String sql = "INSERT INTO TABLE_PATIENT(" +
                "NAME, " +
                "SURNAME, " +
                "PATRONYMIC, " +
                "PHONENUMBER) " +
                "VALUES (?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getSurname());
            preparedStatement.setString(3, entity.getPatronymic());
            preparedStatement.setString(4, entity.getPhoneNumber());
            if (preparedStatement.executeUpdate() != 1) {
                throw new MedicalException("Crating new Patient failed");
            } else {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    resultSet.next();
                    patient.setId(resultSet.getLong(1));
                    patient.setName(entity.getName());
                    patient.setSurname(entity.getSurname());
                    patient.setPatronymic(entity.getPatronymic());
                    patient.setPhoneNumber(entity.getPhoneNumber());
                }
            }
        } catch (Exception e) {
            throw new MedicalException(e);
        }
        return patient;
    }

    @Override
    public Patient getByKey(Long key) throws MedicalException {
        Patient patient = null;
        String sql = "SELECT " +
                "id, " +
                "name, " +
                "surname, " +
                "patronymic, " +
                "phoneNumber " +
                "FROM TABLE_PATIENT WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                patient = new Patient();
                patient.setId(key);
                patient.setName(resultSet.getString("name"));
                patient.setSurname(resultSet.getString("surname"));
                patient.setPatronymic(resultSet.getString("patronymic"));
                patient.setPhoneNumber(resultSet.getString("phoneNumber"));
            }
        } catch (Exception e) {
            throw new MedicalException(e);
        }
        return patient;
    }

    @Override
    public void update(Patient entity) throws MedicalException {
        String sql = "UPDATE TABLE_PATIENT SET " +
                "name = ?, " +
                "surname = ?, " +
                "patronymic = ?, " +
                "phoneNumber = ? " +
                "WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getSurname());
            preparedStatement.setString(3, entity.getPatronymic());
            preparedStatement.setString(4, entity.getPhoneNumber());
            preparedStatement.setLong(5, entity.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new MedicalException(e);
        }
    }

    @Override
    public void delete(Patient entity) throws MedicalException {
        String sql = "DELETE FROM TABLE_PATIENT WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, entity.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new MedicalException(e);
        }
    }

    @Override
    public List<Patient> getAll() throws MedicalException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT " +
                "id, " +
                "name, " +
                "surname, " +
                "patronymic, " +
                "phoneNumber " +
                "FROM TABLE_PATIENT";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Patient patient = new Patient();
                patient.setId(resultSet.getLong("id"));
                patient.setName(resultSet.getString("name"));
                patient.setSurname(resultSet.getString("surname"));
                patient.setPatronymic(resultSet.getString("patronymic"));
                patient.setPhoneNumber(resultSet.getString("phoneNumber"));
                list.add(patient);
            }
        } catch (Exception e) {
            throw new MedicalException(e);
        }
        return list;
    }
}
