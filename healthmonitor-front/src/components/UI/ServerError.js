import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React from "react";
import classes from './ServerError.module.css';
import { faExclamationTriangle } from "@fortawesome/free-solid-svg-icons"
import Card from "./Card";

const ServerError = (props) => {
    return (
            <Card className={classes.outerContainer}>
                <div className={classes.container}>
                    <div><FontAwesomeIcon className={`${classes.errorIcon} fa-3x`} icon={faExclamationTriangle}/></div>
                    <div className={classes.text}>{props.children}</div>
                </div>
            </Card>
        );
};

export default ServerError;