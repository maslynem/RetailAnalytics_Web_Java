package ru.school.retailanalitycs_web_java.repositories.viewRepositories;

import ru.school.retailanalitycs_web_java.entities.views.groupView.GroupView;
import ru.school.retailanalitycs_web_java.entities.views.groupView.GroupViewId;

import java.util.List;

public interface GroupViewRepository extends ReadOnlyRepository<GroupView, GroupViewId> {
    List<GroupView> findAllByOrderByIdAsc();
}