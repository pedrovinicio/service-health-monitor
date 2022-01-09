import React from "react";
import classes from './AppHeader.module.css';

const AppHeader = (props) => {
    return <div className={classes.header}>{props.children}</div>
};

export default AppHeader;