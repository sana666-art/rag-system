package com.rag_system.type;

import com.pgvector.PGvector;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class VectorType implements UserType<PGvector>, Serializable {

    private static final int SQL_TYPE = Types.OTHER;

    @Override
    public int getSqlType() {
        return SQL_TYPE;
    }

    @Override
    public Class<PGvector> returnedClass() {
        return PGvector.class;
    }

    @Override
    public boolean equals(PGvector x, PGvector y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(PGvector x) {
        return Objects.hashCode(x);
    }

    @Override
    public PGvector nullSafeGet(ResultSet rs, int position,
                                 SharedSessionContractImplementor session,
                                 Object owner)
            throws SQLException {

        String value = rs.getString(position);
        if (value == null) {
            return null;
        }
        return new PGvector(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, PGvector value,
                            int index,
                            SharedSessionContractImplementor session)
            throws SQLException {

        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            PGobject pgObject = new PGobject();
            pgObject.setType("vector");
            pgObject.setValue(value.toString());
            st.setObject(index, pgObject);
        }
    }

    @Override
    public PGvector deepCopy(PGvector value) {
        if (value == null) {
            return null;
        }
        try {
            return new PGvector(value.toString());
        } catch (SQLException e) {
            throw new HibernateException("Failed to deep copy pgvector", e);
        }
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(PGvector value) {
        return deepCopy(value);
    }

    @Override
    public PGvector assemble(Serializable cached, Object owner) {
        if (cached == null) {
            return null;
        }
        try {
            return new PGvector(cached.toString());
        } catch (SQLException e) {
            throw new HibernateException("Failed to assemble pgvector", e);
        }
    }
}
