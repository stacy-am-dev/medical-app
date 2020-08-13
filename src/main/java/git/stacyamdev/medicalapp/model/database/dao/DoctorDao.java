package git.stacyamdev.medicalapp.model.database.dao;

import git.stacyamdev.medicalapp.model.entity.Doctor;
import git.stacyamdev.medicalapp.model.exception.MedicalException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DoctorDao implements DaoInterface<Doctor> {

    private final Connection connection;

    public DoctorDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void persist(Doctor entity) throws MedicalException {
        Doctor doctor = new Doctor();
        String sql = "INSERT INTO TABLE_DOCTOR(" +
                "NAME, " +
                "SURNAME, " +
                "PATRONYMIC, " +
                "SPECIALIZATION) " +
                "VALUES (?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getSurname());
            preparedStatement.setString(3, entity.getPatronymic());
            preparedStatement.setString(4, entity.getSpecialization());
            if (preparedStatement.executeUpdate() != 1) {
                throw new MedicalException("Crating new Doctor failed");
            } else {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    resultSet.next();
                    doctor.setId(resultSet.getLong(1));
                    doctor.setName(entity.getName());
                    doctor.setSurname(entity.getSurname());
                    doctor.setPatronymic(entity.getPatronymic());
                    doctor.setSpecialization(entity.getSpecialization());
                }
            }
        } catch (Exception e) {
            throw new MedicalException(e);
        }
    }

    @Override
    public Doctor getByKey(Long key) throws MedicalException {
        Doctor doctor = null;
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
                doctor = new Doctor();
                doctor.setId(key);
                doctor.setName(resultSet.getString("name"));
                doctor.setSurname(resultSet.getString("surname"));
                doctor.setPatronymic(resultSet.getString("patronymic"));
                doctor.setSpecialization(resultSet.getString("specialization"));
            }
        } catch (Exception e) {
            throw new MedicalException(e);
        }
        return doctor;
    }

    @Override
    public void update(Doctor entity) throws MedicalException {
        String sql = "UPDATE TABLE_DOCTOR SET " +
                "name = ?, " +
                "surname = ?, " +
                "patronymic = ?, " +
                "specialization = ? " +
                "WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getSurname());
            preparedStatement.setString(3, entity.getPatronymic());
            preparedStatement.setString(4, entity.getSpecialization());
            preparedStatement.setLong(5, entity.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new MedicalException(e);
        }
    }

    @Override
    public void delete(Doctor entity) throws MedicalException {
        String sql = "DELETE FROM TABLE_DOCTOR WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, entity.getId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new MedicalException(e);
        }
    }

    @Override
    public List<Doctor> getAll() throws MedicalException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT " +
                "id, " +
                "name, " +
                "surname, " +
                "patronymic, " +
                "specialization " +
                "FROM TABLE_DOCTOR";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(resultSet.getLong("id"));
                doctor.setName(resultSet.getString("name"));
                doctor.setSurname(resultSet.getString("surname"));
                doctor.setPatronymic(resultSet.getString("patronymic"));
                doctor.setSpecialization(resultSet.getString("specialization"));
                list.add(doctor);
            }
        } catch (Exception e) {
            throw new MedicalException(e);
        }
        return list;
    }
}
