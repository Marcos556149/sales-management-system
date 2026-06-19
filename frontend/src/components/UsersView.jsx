import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { RefreshCw, Plus, Edit2, Info, UserCog } from 'lucide-react';
import { useToast } from './ToastContext';
import { useUsersContext } from './UsersContext';
import { userService } from '../services/userService';
import { isAdmin as checkIsAdmin } from '../utils/authUtils';
import { useKeyboardShortcuts } from '../hooks/useKeyboardShortcuts';
import { useMemo } from 'react';
import ChangeUserStatusModal from './ChangeUserStatusModal';

const UsersView = () => {
  const navigate = useNavigate();
  const { addToast } = useToast();

  const isAdmin = checkIsAdmin();

  const {
    usersData,
    setUsersData,
    isCached,
    setIsCached
  } = useUsersContext();



  const [loading, setLoading] = useState(!isCached);
  const [error, setError] = useState(null);

  const [focusedIndex, setFocusedIndex] = useState(-1);

  const [isStatusModalOpen, setIsStatusModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);

  const fetchUsers = async (forceRefresh = false) => {
    if (isCached && !forceRefresh) return;

    setLoading(true);
    setError(null);

    try {
      const response = await userService.getUsers();

      setUsersData(Array.isArray(response.data) ? response.data : []);
      setIsCached(true);
    } catch (err) {
      console.error('Error al cargar usuarios:', err);

      const message =
        err.message ||
        'No se pudieron cargar los usuarios. Intente nuevamente más tarde.';

      setError(message);
      addToast(message, 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [isCached]);

  useEffect(() => {
    setFocusedIndex(-1);
  }, [usersData]);

  useEffect(() => {
    if (focusedIndex >= 0) {
      const row = document.getElementById(`user-row-${focusedIndex}`);
      if (row) {
        row.scrollIntoView({ block: 'nearest' });
      }
    }
  }, [focusedIndex]);

  const handleRefresh = async () => {
    setIsCached(false);
    await fetchUsers(true);
  };

  const handleRowClick = (user) => {
    navigate(
      `/dashboard/users/${user.userId}`,
      {
        state: { user }
      }
    );
  };

  const handleEditClick = (user) => {
    navigate(
      `/dashboard/users/edit/${user.userId}`,
      {
        state: { user }
      }
    );
  };

  const handleChangeStatusClick = (user) => {
    setSelectedUser(user);
    setIsStatusModalOpen(true);
  };

  const handleManualRefresh = () => {
    handleRefresh();
  };

  const handleNewUser = () => {
    navigate('/dashboard/users/new');
  };

  useKeyboardShortcuts(
    useMemo(() => {
      const shortcuts = {
        'ctrl+shift+k': handleManualRefresh,

        arrowdown: () => {
          if (usersData.length > 0) {
            setFocusedIndex(prev =>
              Math.min(prev + 1, usersData.length - 1)
            );
          }
        },

        arrowup: () => {
          if (usersData.length > 0) {
            setFocusedIndex(prev =>
              Math.max(prev - 1, 0)
            );
          }
        },

        enter: () => {
          if (focusedIndex >= 0 && usersData[focusedIndex]) {
            handleRowClick(usersData[focusedIndex]);
          }
        }
      };

      if (isAdmin) {
        shortcuts['alt+n'] = handleNewUser;
      }

      return shortcuts;
    }, [isAdmin, usersData, focusedIndex])
  );

  return (
    <div className="view-container">
      {/* Toolbar */}
      <div className="view-toolbar">
        <div className="toolbar-left">
          <h2>Usuarios</h2>
        </div>

        <div className="toolbar-right">
          <button
            className="btn-secondary"
            onClick={handleRefresh}
            disabled={loading}
          >
            <RefreshCw
              size={18}
              className={loading ? 'spin-animation' : ''}
            />
            <span>
              {loading ? 'Actualizando...' : 'Actualizar'}
            </span>

            <span className="btn-shortcut">Ctrl+Shift+K</span>
          </button>

          {isAdmin && (
            <button
              className="btn-primary"
              onClick={() => navigate('/dashboard/users/new')}
            >
              <Plus size={18} />
              <span>Nuevo Usuario</span>

              <span className="btn-shortcut">Alt+N</span>
            </button>
          )}
        </div>
      </div>

      {/* Table */}
      <div className="table-card">
        {loading ? (
          <div className="loading-state">
            <p>Cargando usuarios...</p>
          </div>
        ) : error ? (
          <div className="error-state">
            <p>{error}</p>
          </div>
        ) : usersData.length === 0 ? (
          <div className="empty-state">
            <p>No se encontraron usuarios</p>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Usuario</th>
                  <th>
                    <div
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '6px'
                      }}
                    >
                      <span>Rol</span>

                      <div
                        className="tooltip-container"
                        tabIndex="0"
                      >
                        <Info
                          size={14}
                          className="info-icon"
                          style={{ cursor: 'help' }}
                        />

                        <span className="tooltip-text">
                          Solo se muestran usuarios con rol de Operador.
                        </span>
                      </div>
                    </div>
                  </th>
                  <th>
                    <div
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '6px'
                      }}
                    >
                      <span>Estado</span>

                      <div
                        className="tooltip-container"
                        tabIndex="0"
                      >
                        <Info
                          size={14}
                          className="info-icon"
                          style={{ cursor: 'help' }}
                        />

                        <span className="tooltip-text">
                          Los usuarios suspendidos o eliminados no pueden acceder al sistema.
                        </span>
                      </div>
                    </div>
                  </th>
                  {isAdmin && <th className="text-right">Acciones</th>}
                </tr>
              </thead>

              <tbody>
                {usersData.map((user, index) => (
                  <tr
                    key={user.userId}
                    id={`user-row-${index}`}
                    className={`interactive-row ${focusedIndex === index ? 'focused' : ''}`}
                    style={{ cursor: 'pointer' }}
                    onClick={() => handleRowClick(user)}
                  >
                    <td className="font-medium">
                      {user.userName}
                    </td>

                    <td>
                      {user.userRole?.label ?? '-'}
                    </td>

                    <td>
                      <span
                        className={`status-badge ${(user.userStatus?.code || '').toLowerCase()}`}
                      >
                        {user.userStatus?.label ?? 'Desconocido'}
                      </span>
                    </td>

                    {isAdmin && (
                      <td
                        className="actions-cell text-right"
                        onClick={(e) => e.stopPropagation()}
                      >
                        <button
                          className="action-btn edit-btn"
                          title="Editar usuario"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleEditClick(user);
                          }}
                        >
                          <Edit2 size={16} />
                        </button>

                        <button
                          className="action-btn status-btn"
                          title="Cambiar estado"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleChangeStatusClick(user);
                          }}
                        >
                          <UserCog size={16} />
                        </button>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <ChangeUserStatusModal
        isOpen={isStatusModalOpen}
        user={selectedUser}
        onClose={() => {
          setIsStatusModalOpen(false);
          setSelectedUser(null);
        }}
        onSuccess={handleRefresh}
      />
    </div>


  );
};

export default UsersView;