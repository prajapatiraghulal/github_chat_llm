package com.example.chatbot.service.impl;

import com.example.chatbot.service.api.VectorDbService;
import com.example.chatbot.utils.CommonUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service
public class VectorDbServiceImpl implements VectorDbService {

    private final JdbcTemplate jdbcTemplate;

    public VectorDbServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> searchRelevantDocs(float[] queryEmbedding) {
        String sql = """
            SELECT content
            FROM documents
            ORDER BY embedding <-> ?::vector
            LIMIT 5
        """;

        return jdbcTemplate.query(
                sql,
                ps -> ps.setObject(1, toPgVector(queryEmbedding)),
                (rs, rowNum) -> rs.getString("content")
        );
    }

    // Convert float[] to PGobject of type vector
    private PGobject toPgVector(float[] embedding) throws SQLException {
        PGobject vectorObj = new PGobject();
        vectorObj.setType("vector");
        vectorObj.setValue(CommonUtil.getString(embedding)); // "[0.1,0.2,...]"
        return vectorObj;
    }
}
