package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.KenyaEMRMaritalStatusDataDefinition;
import org.openmrs.module.reporting.data.visit.EvaluatedVisitData;
import org.openmrs.module.reporting.data.visit.definition.VisitDataDefinition;
import org.openmrs.module.reporting.data.visit.evaluator.VisitDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates a VisitIdDataDefinition to produce a VisitData
 */
@Handler(supports=KenyaEMRMaritalStatusDataDefinition.class, order=50)
public class KenyaEMRMaritalStatusDataEvaluator implements VisitDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedVisitData evaluate(VisitDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedVisitData c = new EvaluatedVisitData(definition, context);

        String qry = "select e.patient_id ,\n" +
                "mid(max(concat(date(o.obs_datetime), cn.name)), 11) as marital_status\n" +
                "from patient p  inner join encounter e on e.patient_id = p.patient_id  \n" +
                "inner join obs o on o.encounter_id = e.encounter_id and o.voided=0  \n" +
                "left outer join concept_name cn on cn.concept_id=o.value_coded  and cn.concept_name_type='FULLY_SPECIFIED'\n" +
                "    and cn.locale='en'\n" +
                "where o.concept_id =1054 and e.patient_id  \n" +
                "group by e.patient_id\n";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        //queryBuilder.addParameter("patientIds", ModuleFileProcessorUtil.defaultCohort());
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        System.out.println("Completed processing marital status");
        return c;
    }
}
