/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.crystalball.simulator.impl.persistence.entity;

import java.util.List;

import org.activiti.crystalball.simulator.impl.ResultQueryImpl;
import org.activiti.crystalball.simulator.impl.persistence.AbstractManager;
import org.activiti.engine.impl.Page;



public class ResultEntityManager extends AbstractManager {

	
	public long findResultCountByQueryCriteria( ResultQueryImpl executionQuery) {
		return (Long) getDbSqlSession().selectOne("selectResultsCountByQueryCriteria", executionQuery);
	}

	@SuppressWarnings("unchecked")
	public List<ResultEntity> findResultsByQueryCriteria(ResultQueryImpl executionQuery, Page page) {
		return getDbSqlSession().selectList("selectResultsByQueryCriteria",	executionQuery, page);
	}
}
