package com.project.petcareapp.repository;

import com.project.petcareapp.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface TaskRepository extends JpaRepository<Task,Integer> {


//        WorkflowTask findByName(String name);// Trong đây làm gì có name mà m findByName...??

        @Query("SELECT tasks FROM Task tasks WHERE tasks.workflow.id =: workflowId")
        List<Task> findAllWorkflowByStatus(@Param("workflowId") int workflowId);

        Task findWorkflowById(Integer id);

        Task findTaskByPreTaskAndWorkflow_Id(String pre, int workflow);

        Task findTaskByPostTaskAndWorkflow_Id(String post, int workflow);

        @Query("SELECT tasks.preTask FROM Task tasks WHERE tasks.workflow.id  = :workflowId AND tasks.shapeId = :shapeId")
        String findPreTask(@Param("workflowId") int workflowId, @Param("shapeId") String shapeId);

        Task findTaskByPreTask(String preTask);
        Task findTaskByShapeId(String shapeId);

        Task findTaskByShapeIdAndWorkflow_Id(String shape_id, int workflow);
//        List<WorkflowTask> findAllByTaskId(int id);


        Task findTaskByWorkflowIdAndShapeId(int workflowId, String shapeId);

//        @Query("SELECT task FROM Task task WHERE task.workflow.id = :workflowId AND task.shapeId= :shapeId")
//        Task findTaskByWorkflowIdAnd(@Param("subcriberId")int subcriberId,@Param("groupContactId") int groupContactId );


}
