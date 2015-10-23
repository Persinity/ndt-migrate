/*
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.persinity.ndt.dbdiff.rel;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.persinity.common.Resource;
import com.persinity.common.logging.Log4jLogger;
import com.persinity.ndt.dbdiff.TransformEntity;
import com.persinity.ndt.dbdiff.TransformEntityStore;

/**
 * <p/>
 * Implements reading and writing the TransformEntities as json file in format:
 * <p/>
 * <p/>
 * <pre>
 * {
 *   "transformations": [
 *     {
 *       "targetEntity": "...",
 *       "transformStatement": "...",
 *       "sourceLeadingEntity": "...",
 *       "sourceLeadingColumns": ["col...", "col..."]
 *     },
 *     {
 *       "targetEntity": "...",
 *       "transformStatement": "...",
 *       "sourceLeadingEntity": "...",
 *       "sourceLeadingColumns": ["col...", "col..."]
 *     },
 *     ...
 *   ]
 * }
 * </pre>
 *
 * @author Ivan Dachev
 */
public class JsonTransformEntityStore implements TransformEntityStore {
    public static final String TRANSFORMATIONS_KEY = "transformations";
    public static final String TARGET_ENTITY_KEY = "targetEntity";
    public static final String TRANSFORM_STATEMENT_KEY = "transformStatement";
    public static final String SOURCE_LEADING_ENTITY_KEY = "sourceLeadingEntity";
    public static final String SOURCE_LEADING_COLUMNS_KEY = "sourceLeadingColumns";

    public JsonTransformEntityStore(final String sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Override
    public Collection<TransformEntity> loadTransformEntities() {
        return resource.accessAndAutoClose(new Resource.Accessor<InputStream, LinkedHashSet<TransformEntity>>() {
            @Override
            public InputStream getResource() {
                final InputStream fis;
                try {
                    fis = new FileInputStream(sourceFile);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                return new BufferedInputStream(fis);
            }

            @Override
            public LinkedHashSet<TransformEntity> access(final InputStream is) throws Exception {
                final LinkedHashSet<TransformEntity> transformEntities = new LinkedHashSet<>();
                final String jsonTxt = IOUtils.toString(is);
                final JSONObject obj = new JSONObject(jsonTxt);
                final JSONArray array = obj.getJSONArray(TRANSFORMATIONS_KEY);
                for (int i = 0; i < array.length(); i++) {
                    final JSONObject element = array.getJSONObject(i);
                    final String targetEntity = element.getString(TARGET_ENTITY_KEY);
                    final String transformStatement = element.getString(TRANSFORM_STATEMENT_KEY);
                    final String sourceLeadingEntity = element.getString(SOURCE_LEADING_ENTITY_KEY);
                    final Set<String> sourceLeadingColumns = convertToStringSet(
                            element.getJSONArray(SOURCE_LEADING_COLUMNS_KEY));
                    final TransformEntity transformEntity = new TransformEntity(targetEntity, transformStatement,
                            sourceLeadingEntity, sourceLeadingColumns);
                    transformEntities.add(transformEntity);
                }
                log.info("Read transformations: {} from file: {}", transformEntities.size(), sourceFile);
                return transformEntities;
            }
        });
    }

    @Override
    public void saveTransformEntities(final Collection<TransformEntity> transformEntities) {
        resource.accessAndAutoClose(new Resource.Accessor<BufferedWriter, Void>() {
            @Override
            public BufferedWriter getResource() {
                final Writer w;
                try {
                    w = new FileWriter(sourceFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return new BufferedWriter(w);
            }

            @Override
            public Void access(final BufferedWriter bw) throws Exception {
                final JSONArray transformations = new JSONArray();
                int i = 0;
                for (final TransformEntity transformEntity : transformEntities) {
                    final JSONObject obj = new JSONObject();
                    obj.put(TARGET_ENTITY_KEY, transformEntity.getTargetEntity());
                    obj.put(TRANSFORM_STATEMENT_KEY, transformEntity.getTransformStatement());
                    obj.put(SOURCE_LEADING_ENTITY_KEY, transformEntity.getSourceLeadingEntity());
                    obj.put(SOURCE_LEADING_COLUMNS_KEY, convertToJsonArray(transformEntity.getSourceLeadingColumns()));
                    transformations.put(i, obj);
                    i++;
                }
                final JSONObject obj = new JSONObject();
                obj.put(TRANSFORMATIONS_KEY, transformations);
                bw.write(obj.toString());
                log.info("Wrote transformations: {} to file: {}", transformEntities.size(), sourceFile);
                return null;
            }
        });
    }

    private Set<String> convertToStringSet(final JSONArray jsonArray) {
        final LinkedHashSet<String> res = new LinkedHashSet<>();
        for (int j = 0; j < jsonArray.length(); j++) {
            res.add(jsonArray.getString(j));
        }
        return res;
    }

    private JSONArray convertToJsonArray(final Set<String> set) {
        final JSONArray res = new JSONArray();
        int i = 0;
        for (String value : set) {
            res.put(i, value);
            i++;
        }
        return res;
    }

    private final String sourceFile;

    private static final Resource resource = new Resource();
    private static final Log4jLogger log = new Log4jLogger(Logger.getLogger(JsonTransformEntityStore.class));
}
