import React from 'react';
import TableCell from '@material-ui/core/TableCell';

import TableRow from '@material-ui/core/TableRow';
import {Button} from "@material-ui/core";
import Snackbar from '@material-ui/core/Snackbar';

export default function BasicTable(props) {

    const [open, setOpen] = React.useState(false);
    const [message, setMessage] = React.useState("");


    const uploadToEngine = (event, key) => {
        setOpen(true);
        setMessage("The workflow has been uploaded to the engine!")
        props.tableValues[key].uploadStatus = "UPLOADED"
    };

    const startOrchestration = (event, key) => {
        setOpen(true);
        setMessage("The deployment has started!")
        props.tableValues[key].startedStatus = "STARTED"

    }

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }

        setOpen(false);
    };

    return (
        props.tableValues && props.tableValues.map((row, i) => (
            <TableRow key={i}>
                <TableCell component="th" scope="row">
                    {row.deploymentName}
                </TableCell>
                <TableCell align="right">{row.edmmID}</TableCell>
                <TableCell align="right">{row.uploadStatus}</TableCell>
                <TableCell align="right">{row.startedStatus}</TableCell>
                <TableCell align="right">
                    <Button variant="outlined" onClick={(e) => uploadToEngine(e, i)} style={{color: "#0277BD"}}>Upload To Engine</Button></TableCell>
                <TableCell align="right">
                    <Button variant="outlined"  onClick={(e) => startOrchestration(e, i)} style={{color: "#0277BD"}}>Start Orchestration</Button></TableCell>
                <Snackbar
                    anchorOrigin={{
                        vertical: 'bottom',
                        horizontal: 'left',
                    }}
                    open={open}
                    autoHideDuration={2000}
                    onClose={handleClose}
                    message= {message}
                />
            </TableRow>
        ))
    );
}