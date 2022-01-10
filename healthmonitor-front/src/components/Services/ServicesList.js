import React from "react";

import Date from "../UI/Date";
import Card from "../UI/Card";
import classes from './ServicesList.module.css';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTrashAlt, faEdit } from "@fortawesome/free-solid-svg-icons"

const ServicesList = props => {
    const onRemoveServiceHandler = (service) => {
        return () => props.onRemoveService(service);
    }

    const onUpdateServiceHandler = (service) => {
        return () => props.onUpdateService(service);
    }

    return (
        <Card data-testid="service-list" className={classes.services}>
            <div className={classes.container}>
                {props.services.map((service) =>(
                    <div key={service.Id} className={service.Valid ? classes.itemGreen : classes.itemRed}>
                        <FontAwesomeIcon className={classes.actionButtons} icon={faTrashAlt}  onClick={onRemoveServiceHandler(service)}/>
                        <FontAwesomeIcon className={classes.actionButtons} icon={faEdit}  onClick={onUpdateServiceHandler(service)}/>
                        <div className={classes.itemPrimary}>{service.Name}</div>
                        <div className={classes.itemSecondary}>{service.Url}</div>
                        <div className={classes.itemSecondarySpaced}>Added on <Date date={service.Created}/></div>
                        <div className={classes.itemSecondary}>Last verified on <Date date={service.LastVerified}/></div>
                    </div>
                ))}
            </div>
        </Card>
    );
};

export default ServicesList;